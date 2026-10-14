create index IX_C5A6C78F on BackgroundTask (companyId);
create index IX_FBF5FAA2 on BackgroundTask (completed);
create index IX_C71C3B7 on BackgroundTask (groupId, status);
create index IX_F6136B70 on BackgroundTask (groupId, taskExecutorClassName[$COLUMN_LENGTH:200$], completed, name[$COLUMN_LENGTH:255$]);
create index IX_7FC3A8C9 on BackgroundTask (groupId, taskExecutorClassName[$COLUMN_LENGTH:200$], name[$COLUMN_LENGTH:255$]);
create index IX_7E757D70 on BackgroundTask (groupId, taskExecutorClassName[$COLUMN_LENGTH:200$], status);
create index IX_2FCFE748 on BackgroundTask (taskExecutorClassName[$COLUMN_LENGTH:200$], status);