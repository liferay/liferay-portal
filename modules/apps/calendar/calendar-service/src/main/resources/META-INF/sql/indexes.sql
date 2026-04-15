create index IX_97FC174E on Calendar (groupId, calendarResourceId, defaultCalendar);
create unique index IX_31D79378 on Calendar (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_BD5AA0AC on CalendarBooking (calendarId, parentCalendarBookingId, ctCollectionId);
create index IX_470170B4 on CalendarBooking (calendarId, status);
create unique index IX_8AE4D46C on CalendarBooking (calendarId, vEventUid[$COLUMN_LENGTH:255$], ctCollectionId);
create index IX_B198FFC on CalendarBooking (calendarResourceId);
create unique index IX_521307D2 on CalendarBooking (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_F7B8A941 on CalendarBooking (parentCalendarBookingId, status);
create index IX_14ADC52E on CalendarBooking (recurringCalendarBookingId);
create unique index IX_1554DFF5 on CalendarBooking (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_7727A482 on CalendarNotificationTemplate (calendarId, notificationType[$COLUMN_LENGTH:75$], notificationTemplateType[$COLUMN_LENGTH:75$]);
create unique index IX_10D0E1DD on CalendarNotificationTemplate (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_74AD9DDD on CalendarResource (active_, code_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_CD46CB85 on CalendarResource (classNameId, classPK, ctCollectionId);
create index IX_40678371 on CalendarResource (groupId, active_);
create index IX_55C2F8AA on CalendarResource (groupId, code_[$COLUMN_LENGTH:75$]);
create unique index IX_FD05567A on CalendarResource (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_150E2F22 on CalendarResource (uuid_[$COLUMN_LENGTH:75$]);