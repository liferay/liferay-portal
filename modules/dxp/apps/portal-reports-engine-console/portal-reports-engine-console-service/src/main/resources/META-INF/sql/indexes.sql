create index IX_6C1481B1 on Reports_Definition (groupId);
create unique index IX_D4E3828F on Reports_Definition (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_C5A9E1E9 on Reports_Source (groupId);
create unique index IX_A3C3F357 on Reports_Source (uuid_[$COLUMN_LENGTH:75$], groupId);