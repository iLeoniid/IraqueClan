package gg.leo.IraqueClan.utils.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuPagination<T> {
    private final List<T> items;
    private final int pageSize;
    private final int currentPage;

    public MenuPagination(List<T> items, int pageSize, int currentPage) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.pageSize = Math.max(1, pageSize);
        this.currentPage = Math.max(1, currentPage);
    }

    public List<T> getItemsForPage() {
        int totalPages = this.getTotalPages();
        int safePage = Math.min(this.currentPage, Math.max(1, totalPages));
        int startIndex = (safePage - 1) * this.pageSize;
        int endIndex = Math.min(startIndex + this.pageSize, this.items.size());
        if (startIndex >= this.items.size()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(this.items.subList(startIndex, endIndex));
    }

    public boolean hasPreviousPage() {
        return this.currentPage > 1;
    }

    public boolean hasNextPage() {
        return this.currentPage < this.getTotalPages();
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getTotalPages() {
        if (this.items.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) this.items.size() / this.pageSize);
    }

    public static int clampPage(int page, int totalPages) {
        if (totalPages <= 1) {
            return 1;
        }
        return Math.max(1, Math.min(page, totalPages));
    }
}
