package com.air.nz.models.mailbox;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;

@Schema(description = "Mailbox Folder Item - Pagination Details.")
public class FolderItemPagination {

    private final String FIELD_EMPTY_MESSAGE = "Field cannot be empty.";
    
    @Schema(description = "Total Number of Records.")
    @NotNull
    private BigInteger totalRecords;

    @Schema(description = "Number of Pages.")
    @NotNull
    private Integer totalPages;

    @Schema(description = "The page size.")
    @NotNull
    private Integer pageSize;

    public FolderItemPagination() {
    }

    public BigInteger getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(BigInteger totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
