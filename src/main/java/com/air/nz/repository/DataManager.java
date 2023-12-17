package com.air.nz.repository;

import com.air.nz.exceptions.type.*;
import com.air.nz.models.mailbox.FolderItem;
import com.air.nz.models.mailbox.FolderItemSearchResults;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataManager {
    public DataManager() throws Exception {
        loadAvailableFolderItems();
    }

    final Logger logger = LoggerFactory.getLogger(DataManager.class);

    final String DATA_FILE_PATH = "/mailBoxFolderItems/items.json";
    final String LINE_SEPERATOR = "line.separator";
    final String DATA_RESOURCE_FOLDER_DOES_NOT_EXIST = "Source Data File does not exist";

    final String NO_FOLDER_ITEM_DATA = "No folder item data exists in the memory.";

    final String NEW_LINE = "\n";
    final String EXCEPTION = "EXCEPTION: ";
    final String STACK_TRACE = "STACK TRACE: ";
    public static List<FolderItem> availableFolderItems = Collections.synchronizedList(new ArrayList<FolderItem>());

    //region Test Set Up and Tear Down
    public boolean loadAvailableFolderItems() throws Exception {

        File file = getFileFromRelativePath(DATA_FILE_PATH);
        if(!file.exists()) {
            throw new Exception(DATA_RESOURCE_FOLDER_DOES_NOT_EXIST + file.getName());
        }
        synchronized (availableFolderItems) {
            availableFolderItems.clear();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());

            String readContent = getTextFromFile(DATA_FILE_PATH);
            availableFolderItems = mapper.readValue(readContent, new TypeReference<List<FolderItem>>() {
            });

            //Arrange by ReceivedSentOrSaveTimeStamp
            Comparator<FolderItem> comparatorDesc = (folderItem1, folderItem2) -> folderItem2.getReceivedSentOrSavedTimeStamp()
                    .compareTo(folderItem1.getReceivedSentOrSavedTimeStamp());
            Collections.sort(availableFolderItems, comparatorDesc);
        }
        return true;
    }

    public boolean unloadAvailableFolderItems() throws Exception {
        synchronized (availableFolderItems) {
            if (!availableFolderItems.isEmpty())
                availableFolderItems.clear();
        }
        return true;
    }
    //endregion

    public FolderItemSearchResults getEmailForUser(String userEmail, String folder, int lastFolderItemId, int pageSize, int currentPage) {

        synchronized (availableFolderItems) {

            if (availableFolderItems.size() > 0) {

                //Initialize total records
                BigInteger totalRecords;

                //Get the mailbox folder items matching the mail id and sort
                List<FolderItem> emailsMatchingMailId = availableFolderItems.stream().filter(f -> (f.getMailboxOwnerEmail().equals(userEmail))).collect(Collectors.toList());
                Collections.sort(emailsMatchingMailId, Comparator.comparing(FolderItem::getFolderItemId));

                if (emailsMatchingMailId.size() > 0) {

                    //Get the items matching the folder
                    List<FolderItem> emailsMatchingInbox = emailsMatchingMailId.stream().filter(f -> (f.getMailboxFolder().toString().equals(folder))).collect(Collectors.toList());

                    //Excluded previous pages
                    List<FolderItem> emailsExcludingPreviouslyViewedMails = emailsMatchingInbox.stream().filter(e -> e.getFolderItemId().intValue() > lastFolderItemId).collect(Collectors.toList());

                    //Select only the page size
                    List<FolderItem> emailsWithPageSizeApplied = emailsExcludingPreviouslyViewedMails.stream().limit(pageSize).collect(Collectors.toList());

                    //Calculate the pagination
                    if (emailsWithPageSizeApplied.size() > 0) {
                        FolderItemSearchResults folderItemSearchResults = new FolderItemSearchResults();

                        totalRecords = BigInteger.valueOf(emailsMatchingInbox.size());
                        Integer availablePages = totalRecords.intValue() / pageSize;
                        boolean resultsLessThanPageSize = false;

                        //If the number of records are less than the page size
                        if (availablePages == 0) {
                            resultsLessThanPageSize = true;
                            availablePages = 1;
                        }

                        // an extra page to display the remaining remainder of the records
                        if (!resultsLessThanPageSize && (totalRecords.intValue() % pageSize) > 0) {
                            folderItemSearchResults.setTotalPages(availablePages + 1);
                        } else {
                            folderItemSearchResults.setTotalPages(availablePages);
                        }

                        //Generate the results
                        folderItemSearchResults.setFolderItems(emailsWithPageSizeApplied);
                        folderItemSearchResults.setCurrentPage(currentPage + 1);
                        folderItemSearchResults.setPageSize(pageSize);
                        folderItemSearchResults.setTotalRecords(totalRecords);
                        folderItemSearchResults.setFolderItemIdOfLastRecordInPage(emailsWithPageSizeApplied.get(emailsWithPageSizeApplied.size() - 1).getFolderItemId());
                        return folderItemSearchResults;
                    } else
                        throw new NoEmailFound(userEmail, folder);
                } else throw new NoEmailFound(userEmail, folder);
            }
        }
        throw new InternalFailure(NO_FOLDER_ITEM_DATA);
    }

    public FolderItem getFolderItem(BigInteger folderItemId){
        synchronized (availableFolderItems) {
            if (availableFolderItems.size() > 0) {
                //Get the mailbox folder items matching the folder Item id
                List<FolderItem> emailsMatchingFolderItemId = availableFolderItems.stream().filter(f -> (f.getFolderItemId().equals(folderItemId))).collect(Collectors.toList());

                if (emailsMatchingFolderItemId.size() == 0)
                    throw new ResourceNotFound(folderItemId.toString());
                if (emailsMatchingFolderItemId.size() > 1)
                    throw new CorruptData("More than one records exist for the id " + folderItemId);
                if (emailsMatchingFolderItemId.size() == 1)
                    return emailsMatchingFolderItemId.get(0);

            }
        }
        throw new InternalFailure(NO_FOLDER_ITEM_DATA);
    }
    public FolderItem getDraftFromMemory(BigInteger folderItemId){


        synchronized (availableFolderItems) {

            if (availableFolderItems.size() > 0) {
                //Get the mailbox folder items matching the folder Item id
                List<FolderItem> emailsMatchingFolderItemId = availableFolderItems.stream().filter(f -> (f.getFolderItemId().equals(folderItemId))).collect(Collectors.toList());
                List<FolderItem> emailsFromDrafts = emailsMatchingFolderItemId.stream().filter(f -> (f.getMailboxFolder().equals(FolderItem.MailboxFolder.DRAFTS))).collect(Collectors.toList());
                List<FolderItem> emailsFromDraftsUnsent = emailsFromDrafts.stream().filter(f -> (f.getFolderItemState().equals(FolderItem.FolderItemState.UN_SENT))).collect(Collectors.toList());

                if (emailsFromDraftsUnsent.size() > 1)
                    throw new CorruptData("More than one records exist for the id " + folderItemId);
                else if (emailsFromDraftsUnsent.size() == 1)
                    return emailsFromDraftsUnsent.get(0);
            }
        }
        throw new ResourceNotFound(folderItemId.toString());
    }
    public BigInteger saveNewDraftInMemory(FolderItem folderItem){

        synchronized (availableFolderItems){

            List<FolderItem> draftsMatchingSenderEmail = availableFolderItems.stream().filter(f -> (f.getSenderEmail().equals(folderItem.getSenderEmail()))).collect(Collectors.toList());
            List<FolderItem> draftsMatchingSubject = draftsMatchingSenderEmail.stream().filter(f -> (f.getSubject().equals(folderItem.getSubject()))).collect(Collectors.toList());
            Collections.sort(draftsMatchingSubject, Comparator.comparing(FolderItem::getFolderItemId));

            if (draftsMatchingSubject.size() == 1){
                //There will be no folder item id in the request, hence we need to set it
                folderItem.setFolderItemId(draftsMatchingSubject.get(0).getFolderItemId());
                return updateDraftInMemory(folderItem);
            }else{
                //Sort the list based on folderItemId
                Collections.sort(availableFolderItems, Comparator.comparing(FolderItem::getFolderItemId));
                //generate FolderItemId
                BigInteger newFolderItemId = availableFolderItems.get(availableFolderItems.size()-1).getFolderItemId().add(BigInteger.ONE);
                folderItem.setFolderItemId(newFolderItemId);
                availableFolderItems.add(folderItem);
                return newFolderItemId;
            }

        }
    }

    public BigInteger updateDraftItemInMemory(FolderItem folderItem){

        synchronized (availableFolderItems){

            List<FolderItem> draftsMatchingSenderEmail = availableFolderItems.stream().filter(f -> (f.getSenderEmail().equals(folderItem.getSenderEmail()))).collect(Collectors.toList());
            List<FolderItem> draftsMatchingSubject = draftsMatchingSenderEmail.stream().filter(f -> (f.getSubject().equals(folderItem.getSubject()))).collect(Collectors.toList());
            Collections.sort(draftsMatchingSubject, Comparator.comparing(FolderItem::getFolderItemId));

            if (draftsMatchingSubject.size() == 1){
                //There will be no folder item id in the request, hence we need to set it
                folderItem.setFolderItemId(draftsMatchingSubject.get(0).getFolderItemId());
                return updateDraftInMemory(folderItem);
            }else{
                throw new ResourceNotFound("");
            }

        }
    }
    public BigInteger sendNewEmail(FolderItem folderItem) {

        folderItem.setMailboxFolder(FolderItem.MailboxFolder.SENT);
        folderItem.setFolderItemState(FolderItem.FolderItemState.SENT);

        synchronized (availableFolderItems) {
            BigInteger newFolderItemId = availableFolderItems.get(availableFolderItems.size()-1).getFolderItemId().add(BigInteger.ONE);
            folderItem.setFolderItemId(newFolderItemId);
            availableFolderItems.add(folderItem);
        }
        logger.info("EMAIL SENT");
        return folderItem.getFolderItemId();
    }

    public BigInteger sendEmailFromDrafts(FolderItem folderItem) {

        folderItem.setMailboxFolder(FolderItem.MailboxFolder.SENT);
        folderItem.setFolderItemState(FolderItem.FolderItemState.SENT);
        updateMatchingObjectsInMemory(folderItem);
        return folderItem.getFolderItemId();
    }


    private BigInteger updateDraftInMemory(FolderItem folderItem) {

        updateMatchingObjectsInMemory(folderItem);
        return folderItem.getFolderItemId();
    }

    public void updateMatchingObjectsInMemory(FolderItem folderItem){
        //replace the objects in memory which have the same folder item id
        synchronized (availableFolderItems) {
            for (int i=0; i< availableFolderItems.size(); i++) {
                if (availableFolderItems.get(i).getFolderItemId().equals(folderItem.getFolderItemId())){
                    availableFolderItems.set(i, folderItem);
                }
            }
        }
    }

    //region Print Stack Trace
    public String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append(EXCEPTION + e.toString() + NEW_LINE + STACK_TRACE + NEW_LINE);
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append(NEW_LINE);
        }
        return sb.toString();
    }
    //endregion


    //region FILE and STRING HELPERS
    public File getFileFromRelativePath(String relativePath) throws IOException {

        //Get the absolute path and create the file
        File file = null;
        String resource = relativePath;
        URL res = DataManager.class.getResource(resource);

       if (res == null){
            throw new RuntimeException("Error: Unable to get the resource from path: " + resource);
        }

        else if (res.toString().startsWith("jar:"))
        {
            try {
                InputStream input = DataManager.class.getResourceAsStream(resource);
                file = File.createTempFile(new Date().getTime()+"", ".tmp");
                OutputStream out = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.flush();
                out.close();
                input.close();
                file.deleteOnExit();
            } catch (IOException ex) {
                ex.printStackTrace();
                throw ex;
            }
        } else {
            //this will probably work in your IDE, but not from a JAR
            file = new File(res.getFile());
        }

        if (file != null && !file.exists()) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }
        return file;
    }

    public String getTextFromFile(String relativePath) throws IOException {
        String content = null;
        InputStream inputStream = DataManager.class.getResourceAsStream(relativePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        content = reader.lines().collect(Collectors.joining(System.getProperty(LINE_SEPERATOR)));
        reader.close();
        inputStream.close();
        return content;
    }
    //endregion
}
