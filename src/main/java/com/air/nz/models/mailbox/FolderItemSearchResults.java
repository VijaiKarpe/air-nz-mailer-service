package com.air.nz.models.mailbox;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.List;

public class FolderItemSearchResults {

    @Schema(description = "Total Number of Records.")
    private BigInteger totalRecords;
    @Schema(description = "Indicates the current page.")
    @NotNull
    private Integer currentPage;

    @Schema(description = "The page size.")
    @NotNull
    private Integer pageSize;

    @Schema(description = "Total Pages.")
    @NotNull
    private Integer totalPages;

    @Schema(description = "The folder item ID in the last record of the paged set.")
    @NotNull
    private BigInteger folderItemIdOfLastRecordInPage;

    @Schema(description = "The folder items which match a search criteria.")
    @NotNull
    private List<FolderItem> folderItems;

    public FolderItemSearchResults() {
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public BigInteger getFolderItemIdOfLastRecordInPage() {
        return folderItemIdOfLastRecordInPage;
    }

    public void setFolderItemIdOfLastRecordInPage(BigInteger folderItemIdOfLastRecordInPage) {
        this.folderItemIdOfLastRecordInPage = folderItemIdOfLastRecordInPage;
    }

    public List<FolderItem> getFolderItems() {
        return folderItems;
    }

    public void setFolderItems(List<FolderItem> folderItems) {
        this.folderItems = folderItems;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public BigInteger getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(BigInteger totalRecords) {
        this.totalRecords = totalRecords;
    }
}
