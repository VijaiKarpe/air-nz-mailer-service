package com.air.nz.models.mailbox;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jdk.jfr.Timestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "The context is the view of a mailbox's folder, which display's a list of contents with their attributes")
public class FolderItem {

    public enum FolderItemState{
        UN_SENT("Email is saved as a draft"),
        SENT("Email is sent"),
        UNREAD("Email is un-read"),
        READ("Email is read"),
        UN_DELIVERED("Email is undelivered");

        FolderItemState(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        private String description;

    }

    public enum MailboxFolder{
        INBOX("All email"),
        DRAFTS("Unsent emails"),
        ARCHIVE("Archived emails"),
        SENT("Sent emails"),
        DELETED_ITEMS("Deleted Emails"),
        JUNK("Junk Emails"),
        OTHER("Custom Folder for Email");

        MailboxFolder(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        private String description;

    }

    @Schema(description  = "The item's ID")
    @JsonProperty("folderItemId")
    private BigInteger folderItemId;

    @Schema(description  = "The mailbox owner's email")
    @JsonProperty("mailboxOwnerEmail")
    @Email(message = "not a valid email")
    private String mailboxOwnerEmail;

    @Schema(description  = "The mailbox's folder")
    @JsonProperty("mailboxFolder")
    @NotNull
    private MailboxFolder mailboxFolder;

    @Schema(description  = "The state of the folder item")
    @JsonProperty("folderItemState")
    @NotNull
    private FolderItemState folderItemState;

    @Schema(description  = "The sender's name")
    @JsonProperty("senderName")
    @NotEmpty(message = "Cannot be empty")
    @NotNull
    private String senderName;

    @Schema(description  = "The sender's email")
    @JsonProperty("senderEmail")
    @Email(message = "not a valid email")
    @NotNull
    private String senderEmail;

    @Schema(description  = "The Email's to recipients.")
    @JsonProperty("recipients")
    private List<@NotEmpty @Email(message = "not a valid email")String> recipients;

    @Schema(description  = "The Email's copied recipients.")
    @JsonProperty("copiedTo")
    private List<@Email(message = "not a valid email")String> copiedTo;

    @Schema(description  = "The Email's subject.")
    @JsonProperty("subject")
    @NotEmpty(message = "Cannot be empty")
    @NotNull
    private String subject;

    @Schema(description  = "Indicates if an attachment present to the email")
    @JsonProperty("attachmentExists")
    @NotNull
    private Boolean attachmentExists;

    @Schema(description  = "The Email's content")
    @JsonProperty("bodyContent")
    private String bodyContent;

    @Schema(description  = "Date & Time which the email or draft was received or sent or Saved.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDateTime receivedSentOrSavedTimeStamp;

    public FolderItem() {
    }

    public FolderItemState getFolderItemState() {
        return folderItemState;
    }

    public void setFolderItemState(FolderItemState folderItemState) {
        this.folderItemState = folderItemState;
    }

    public BigInteger getFolderItemId() {
        return folderItemId;
    }

    public void setFolderItemId(BigInteger folderItemId) {
        this.folderItemId = folderItemId;
    }

    public String getMailboxOwnerEmail() {
        return mailboxOwnerEmail;
    }

    public void setMailboxOwnerEmail(String mailboxOwnerEmail) {
        this.mailboxOwnerEmail = mailboxOwnerEmail;
    }

    public MailboxFolder getMailboxFolder() {
        return mailboxFolder;
    }

    public void setMailboxFolder(MailboxFolder mailboxFolder) {
        this.mailboxFolder = mailboxFolder;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public List<String> getCopiedTo() {
        return copiedTo;
    }

    public void setCopiedTo(List<String> copiedTo) {
        this.copiedTo = copiedTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getAttachmentExists() {
        return attachmentExists;
    }

    public void setAttachmentExists(Boolean attachmentExists) {
        this.attachmentExists = attachmentExists;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public LocalDateTime getReceivedSentOrSavedTimeStamp() {
        return receivedSentOrSavedTimeStamp;
    }

    public void setReceivedSentOrSavedTimeStamp(LocalDateTime receivedSentOrSavedTimeStamp) {
        this.receivedSentOrSavedTimeStamp = receivedSentOrSavedTimeStamp;
    }
}