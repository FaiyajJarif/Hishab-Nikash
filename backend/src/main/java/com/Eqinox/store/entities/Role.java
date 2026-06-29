package com.Eqinox.store.entities;

public enum Role {
    USER,    // default — every signup (replaces the old "NORMAL_USER")
    ADMIN,   // internal team / founders
    SUPPORT  // read-only customer-support staff
}