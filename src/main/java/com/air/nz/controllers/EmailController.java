package com.air.nz.controllers;

import com.air.nz.exceptions.type.*;
import com.air.nz.models.mailbox.FolderItem;
import com.air.nz.models.mailbox.FolderItemSearchResults;
import com.air.nz.repository.DataManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Component
@RestController
@Tag(name = "Email")
@RequestMapping("/0.0.1")
public class EmailController extends DataManager {

    @Value("${swaggerInfo.groupName}")
    private String GROUP_NAME;

    @Value("${responseMessages.internalError}")
    private String INTERNAL_ERROR;

    @Value("${responseMessages.invalidPaginationParams}")
    private String INVALID_PARAMS;

    @Value("${responseMessages.invalidFolderItemId}")
    private String INVALID_FOLDER_ITEM_ID;

    @Value("${responseMessages.setFolder}")
    private String SET_FOLDER_TO;

    @Value("${responseMessages.setFolderItemState}")
    private String SET_FOLDER_ITEM_STATE_TO;

    @Value("${responseMessages.noMailRecipients}")
    private String INCLUDE_MAIL_RECIPIENT;

    @Value("${responseMessages.noDraftInUnsentState}")
    private String NO_DRAFT_IN_UN_SENT_STATE;

    private  final Logger logger = LoggerFactory.getLogger(EmailController.class);

    final String DATA_SET_UP_FAILURE = "Failure whilst setting up data. ";

    public EmailController() throws Exception {
    }

    // a. Retrieve the contents of the user's inbox.
    @Operation(summary = "Gets the mail items for an Inbox's folder", description = "Search will based on mail id, folder name and pagination criteria." + "\n" +
            "For the first search please used the values for  lastFolderItemId = 0 and currentPage = 0. Subsequent searches must include the returned pagination in the search." + "\n" +
            "The page size must be greater than 0")
    @GetMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public FolderItemSearchResults getEmailsForInbox(@RequestHeader(value = "x-request-id") String correlationId, @Valid @RequestParam( required = true) FolderItem.MailboxFolder folder, @Valid @NotEmpty @RequestParam( required = true) String email, @Valid @RequestParam( required = true) Integer lastFolderItemId, @Valid @RequestParam( required = true) int pageSize,@Valid @RequestParam( required = true) int currentPage) throws Exception {

        try {
            if (pageSize < 1 || lastFolderItemId < 0 || currentPage < 0){
                throw new BadRequestFailure(INVALID_PARAMS);
            }
            return getEmailForUser(email, String.valueOf(folder), lastFolderItemId, pageSize, currentPage);
        }catch (Exception e){
            if (e instanceof NoEmailFound || e instanceof BadRequestFailure)
            throw e;
            else
                throw new InternalFailure(e.getMessage());
        }
    }

    //b. Retrieve the contents of a single email.
    @Operation(summary = "Gets the mail item", description = "Search will based on folderItemId")
    @GetMapping("/email/item")
    @ResponseStatus(HttpStatus.OK)
    public FolderItem getEmailsForInbox(@RequestHeader(value = "x-request-id") String correlationId, @Valid @RequestParam( required = true) BigInteger folderItemId) throws Exception {

        try {
            if (folderItemId.signum() != 1){
                throw new BadRequestFailure(INVALID_FOLDER_ITEM_ID);
            }
            return getFolderItem(folderItemId);
        }catch (Exception e){
            if (e instanceof ResourceNotFound || e instanceof CorruptData | e instanceof BadRequestFailure)
                throw e;
            else
                throw new InternalFailure(e.getMessage());
        }
    }

    //c. Write a draft email and save it for later.
    @Operation(summary = "Saves a new draft", description = "Creates a new folder item under the drafts folder.")
    @PostMapping("/email/drafts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BigInteger saveDraft(@RequestHeader(value = "x-request-id") String correlationId, @Valid @RequestBody( required = true) FolderItem folderItem) throws Exception {

        try {
            if (folderItem.getMailboxFolder() != FolderItem.MailboxFolder.DRAFTS)
                throw new BadRequestFailure(SET_FOLDER_TO + FolderItem.MailboxFolder.DRAFTS);
            if (folderItem.getFolderItemState() != FolderItem.FolderItemState.UN_SENT)
                throw new BadRequestFailure(SET_FOLDER_ITEM_STATE_TO + FolderItem.FolderItemState.UN_SENT);

            return saveNewDraftInMemory(folderItem);

        }catch (Exception e){
            if ( e instanceof BadRequestFailure)
                throw e;
            else
                throw new InternalFailure(e.getMessage());
        }
    }


    //d. Send an email.
    @Operation(summary = "Send an email", description = "If a folder item id is included, then the application looks for the folder item saved as a draft in memory and sends it" + "\n" +
            "If a folder item id is NOT included, then the application saves it in memory and sends it" )
    @PostMapping("/email/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BigInteger sendEmail(@RequestHeader(value = "x-request-id") String correlationId, @Valid @RequestBody( required = true) FolderItem folderItem) throws Exception {

        try {

            if (folderItem.getRecipients().size() == 0)
                throw new BadRequestFailure(INCLUDE_MAIL_RECIPIENT);
            if (folderItem.getMailboxFolder() != FolderItem.MailboxFolder.DRAFTS)
                throw new BadRequestFailure(SET_FOLDER_TO + FolderItem.MailboxFolder.DRAFTS);
            if (folderItem.getFolderItemState() != FolderItem.FolderItemState.UN_SENT)
                throw new BadRequestFailure(SET_FOLDER_ITEM_STATE_TO + FolderItem.FolderItemState.UN_SENT);

            if (folderItem.getFolderItemId() != null){
                FolderItem draftFromMemory = getDraftFromMemory(folderItem.getFolderItemId());
                if (draftFromMemory != null)
                    return sendEmailFromDrafts(draftFromMemory);
                else
                    throw new BadRequestFailure(NO_DRAFT_IN_UN_SENT_STATE + folderItem.getFolderItemState());
            }else{
                return sendNewEmail(folderItem);
            }

        }catch (Exception e){
            if (e instanceof ResourceNotFound || e instanceof CorruptData | e instanceof BadRequestFailure)
                throw e;
            else
                throw new InternalFailure(e.getMessage());
        }
    }

    //e. Update one or more properties of draft email
    @Operation(summary = "Updates an existing draft", description = "Updates the folder item in the drafts folder based on folder item ID.")
    @PatchMapping("/email/drafts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BigInteger updateDraft(@RequestHeader(value = "x-request-id") String correlationId, @Valid @RequestBody( required = true) FolderItem folderItem) throws Exception {

        try {
            return updateDraftItemInMemory(folderItem);

        }catch (Exception e){
            if (e instanceof ResourceNotFound || e instanceof CorruptData | e instanceof BadRequestFailure)
                throw e;
            else
                throw new InternalFailure(e.getMessage());
        }
    }

}