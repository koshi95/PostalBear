package com.sanduni.koshila.postalbear.util;

import android.provider.BaseColumns;

public final class PostalBearDBContract {
    private PostalBearDBContract() {}

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_NIC = "nic";
        public static final String COLUMN_NAME_FIRSTNAME = "firstname";
        public static final String COLUMN_NAME_LASTNAME = "lastname";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_USERROLE = "userrole";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    public static class Employee implements BaseColumns {
        public static final String TABLE_NAME = "employee";
        public static final String COLUMN_NAME_NIC = "nic";
    }

    public static class PostOffice implements BaseColumns {
        public static final String TABLE_NAME = "postoffice";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DISTRICT = "district";
        public static final String COLUMN_NAME_TELEPHONE = "telephone";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
    }

    public static class Post implements BaseColumns {
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_NAME_REFERENCE_NUMBER = "referencenumber";
        public static final String COLUMN_NAME_SEND_DATE = "senddate"; // Date
        public static final String COLUMN_NAME_CHARGES = "charges";
        public static final String COLUMN_NAME_SENDER_NIC = "sendernic";
        public static final String COLUMN_NAME_SENDER_ADDRESS = "senderaddress";
        public static final String COLUMN_NAME_RECEIVER_ADDRESS = "receiveraddress";
        public static final String COLUMN_NAME_DISTRIBUTOR_NIC = "distributornic";
        public static final String COLUMN_NAME_LAST_POST_OFFICE_ID = "lastpostofficeid"; // Integer
        public static final String COLUMN_NAME_NEXT_POST_OFFICE_ID = "nextpostofficeid"; // Integer
        public static final String COLUMN_NAME_DELIVERED = "delivered"; // boolean

    }

    public static class DistributedPost implements BaseColumns {
        public static final String TABLE_NAME = "distributedpost";
        public static final String COLUMN_NAME_REFERENCE_NUMBER = "referencenumber";
        public static final String COLUMN_NAME_RECEIVER_NIC = "receivernic";
        public static final String COLUMN_NAME_DISTRIBUTED_DATE = "distributeddate"; // Date
    }

    public static class FailedPost implements BaseColumns {
        public static final String TABLE_NAME = "failedpost";
        public static final String COLUMN_NAME_REFERENCE_NUMBER = "referencenumber";
        public static final String COLUMN_NAME_VISITED_DATE = "visiteddate"; // Date
    }
}
