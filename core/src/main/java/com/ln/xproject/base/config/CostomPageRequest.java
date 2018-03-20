//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ln.xproject.base.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class CostomPageRequest extends PageRequest {
    private static final long serialVersionUID = -4541509938956089562L;
    private final Sort sort;

    public CostomPageRequest(int page, int size) {
        this(page, size, (Sort) null);
    }

    public CostomPageRequest(int page, int size, Direction direction, String... properties) {
        this(page, size, new Sort(direction, properties));
    }

    public CostomPageRequest(int page, int size, Sort sort) {
        super(page - 1, size);
        this.sort = sort;
    }

    public Sort getSort() {
        return this.sort;
    }

    public Pageable next() {
        return new PageRequest(this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    public PageRequest previous() {
        return this.getPageNumber() == 0 ? this
                : new PageRequest(this.getPageNumber() - 1, this.getPageSize(), this.getSort());
    }

    public Pageable first() {
        return new PageRequest(0, this.getPageSize(), this.getSort());
    }

}
