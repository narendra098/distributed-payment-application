package com.narendra.paymentsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentsystemApplication {

    // idem potent

    /**
     open Windows terminal as admin execute "wsl" command
     then to start redis: service redis-server start
     then to start kafka:  rpk redpanda start

     imp:
     to see stored keys in redis:
     1) redis-cli
     2) KEYS *

     to delete all data in redis:
     redis-cli FLUSHALL

     to see all messages (payment-topic is my topic name) inside wsl
     rpk topic consume payment-topic --offset start

     to describe a topic:
     rpk topic describe payment-topic

     to set deletion time to 5 mins:
     rpk topic alter-config payment-topic --set retention.ms=300000

     postgre:
     psql -h localhost -U narendra -d paymentdb
     to show all tables: \dt
     to view table data: select * from payment
     q to quit the table data view
     */

    /**
     create a Kafka topic:
     rpk topic create your-topic-name
     */

    /**
     to start postgre:  service postgresql start
     * Starting PostgreSQL 14 database server                                   [ OK ]

     root@Narendra:/mnt/c/Users/Narendra# sudo -u postgres psql
     psql (14.22 (Ubuntu 14.22-0ubuntu0.22.04.1))
     Type "help" for help.

     postgres=# CREATE DATABASE paymentdb;
     CREATE USER narendra WITH PASSWORD 'password123';
     GRANT ALL PRIVILEGES ON DATABASE paymentdb TO narendra;
     \q
     CREATE DATABASE
     CREATE ROLE
     GRANT
     */
	public static void main(String[] args) {
		SpringApplication.run(PaymentsystemApplication.class, args);
	}

}
