----------------------------------------------------------------------------------------
--                           DATABASE OWNER CREATION
----------------------------------------------------------------------------------------
DO
$body$
    BEGIN
        IF NOT EXISTS(SELECT * FROM pg_catalog.pg_user WHERE usename = 'air_nz_mailer_service_0_0_1')
        THEN CREATE ROLE air_nz_mailer_service_0_0_1 WITH PASSWORD 'air_nz_mailer_service_0_0_1'
            LOGIN
            CREATEDB;
            COMMENT ON ROLE air_nz_mailer_service_0_0_1 IS 'air-nz-mailer-service-0.0.1';
        END IF;
    end;
$body$;


SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = ON;
SET check_function_bodies = FALSE;
SET client_min_messages = WARNING;
SET row_security = OFF;


----------------------------------------------------------------------------------------
--                           SCHEMA AND TABLE CREATION
----------------------------------------------------------------------------------------

DROP SCHEMA IF EXISTS email CASCADE;

CREATE SCHEMA email;

ALTER SCHEMA email
    OWNER TO air_nz_mailer_service_0_0_1;

SET search_path = details, pg_catalog;

SET default_tablespace = '';

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

--TABLE CREATION
create table email.folderItems
(
    folderItemId                 bigserial                   not null,
    mailboxOwnerEmail            varchar                     not null,
    mailboxFolder                varchar                     not null,
    folderItemState              varchar                     not null,
    senderName                   varchar                     not null,
    senderEmail                  varchar                     not null,
    recipients                   varchar,
    copiedTo                     varchar,
    subject                      varchar,
    attachmentExists             boolean                     not null,
    bodyContent                  varchar,
    receivedSentOrSavedTimeStamp timestamp without time zone not null

);

comment on table email.folderItems is 'mail folder items data store';
create index unique_index on email.folderItems using btree (
                                                            folderItemId,
                                                            mailboxOwnerEmail,
                                                            mailboxFolder,
                                                            folderItemState,
                                                            senderName,
                                                            senderEmail,
                                                            recipients,
                                                            copiedTo,
                                                            subject,
                                                            attachmentExists,
                                                            bodyContent,
                                                            receivedSentOrSavedTimeStamp);

alter table email.folderItems
    add constraint folder_item_id_pk
        primary key (folderItemId);

cluster email.folderItems using unique_index;

----------------------------------------------------------------------------------------
--                           CREATE FUNCTIONS (USED TO POPULATE DATA BASE AND FOR THE SCENARIO SEND EMAIL)
----------------------------------------------------------------------------------------

-- CREATE FOLDER ITEM
create or replace function z_funk_create_folder_item(mailboxOwnerEmail varchar,
                                                     mailboxFolder varchar,
                                                     folderItemState varchar,
                                                     senderName varchar,
                                                     senderEmail varchar,
                                                     recipients varchar,
                                                     copiedTo varchar,
                                                     subject varchar,
                                                     attachmentExists boolean,
                                                     bodyContent varchar,
                                                     receivedSentOrSavedTimeStamp timestamp without time zone)
    returns bigint
    language sql
as
$$
insert into email.folderItems(
                           mailboxOwnerEmail,
                           mailboxFolder,
                           folderItemState,
                           senderName,
                           senderEmail,
                           recipients,
                           copiedTo,
                           subject,
                           attachmentExists,
                           bodyContent,
                           receivedSentOrSavedTimeStamp)
VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)
returning folderItemId;
$$;

----------------------------------------------------------------------------------------
--                           GET FUNCTIONS (USED IN THE SCENARIO RETRIEVE CONTENTS OF THE USER'S INBOX AND RETRIEVE CONTENTS OF SINGLE EMAIL)
----------------------------------------------------------------------------------------

-- GET FOLDER ITEMS COUNT BY USER EMAIL ID AND FOLDER
Create or replace function z_funk_get_folder_item_count_by_maibox_owner_email_and_folder(mailbox_Owner_Email varchar, mail_Box_Folder varchar) returns int as
$$
begin
    return (select count(*)::int from air_nz.email.folderItems where mailboxOwnerEmail = $1 and mailBoxFolder = $2);
end;
$$ language plpgsql;

-- GET FOLDER ITEMS BY USER EMAIL ID AND FOLDER

Create or replace function z_funk_get_folder_items_by_maibox_owner_email_and_folder(mailbox_Owner_Email varchar, mail_Box_Folder varchar, folder_Item_Id bigint, pageSize int)
    returns table
            (
                folderItemId                 bigserial,
                mailboxOwnerEmail            varchar,
                mailboxFolder                varchar,
                folderItemState              varchar,
                senderName                   varchar,
                senderEmail                  varchar,
                recipients                   varchar,
                copiedTo                     varchar,
                subject                      varchar,
                attachmentExists             boolean,
                bodyContent                  varchar,
                receivedSentOrSavedTimeStamp timestamp without time zone
            )
as
$$
begin
    return query select folderItemId,
                        mailboxOwnerEmail,
                        mailboxFolder,
                        folderItemState,
                        senderName,
                        senderEmail,
                        recipients,
                        copiedTo,
                        subject,
                        attachmentExists,
                        bodyContentr,
                        receivedSentOrSavedTimeStamp

                 from air_nz.email.folderItems
                 where mailboxOwnerEmail = $1 and mailboxFolder = $2
                   and folderItemId > $3
                 order by receivedSentOrSavedTimeStamp desc
                     fetch first $4 rows only;
end;
$$ language plpgsql;


-- GET FOLDER ITEMS BY FOLDER ITEM ID

Create or replace function z_funk_get_folder_items_by_id(folder_Item_Id bigint)
    returns table
            (
                folderItemId                 bigserial,
                mailboxOwnerEmail            varchar,
                mailboxFolder                varchar,
                folderItemState              varchar,
                senderName                   varchar,
                senderEmail                  varchar,
                recipients                   varchar,
                copiedTo                     varchar,
                subject                      varchar,
                attachmentExists             boolean,
                bodyContent                  varchar,
                receivedSentOrSavedTimeStamp timestamp without time zone
            )
as
$$
begin
    return query select folderItemId,
                        mailboxOwnerEmail,
                        mailboxFolder,
                        folderItemState,
                        senderName,
                        senderEmail,
                        recipients,
                        copiedTo,
                        subject,
                        attachmentExists,
                        bodyContentr,
                        receivedSentOrSavedTimeStamp

                 from air_nz.email.folderItems
                 where folderItemId = $1;
end;
$$ language plpgsql;



----------------------------------------------------------------------------------------
--                           UPDATE FUNCTIONS
----------------------------------------------------------------------------------------

-- UPDATE FOLDER ITEM RECIPIENT LIST

create or replace function z_funk_update_user_details(folderItem_Id bigint,
                                                      recipients_list varchar,
                                                      receivedSentOrSaved_TimeStamp timestamp without time zone)
    returns bigint
    language sql
as
$$
update air_nz.email.folderItems
set recipients                              = recipients_list,
    receivedSentOrSavedTimeStamp            = receivedSentOrSaved_TimeStamp
WHERE folderItemId = $1
RETURNING folderItemId;
$$;


