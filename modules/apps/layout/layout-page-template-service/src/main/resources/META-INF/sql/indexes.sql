create unique index IX_210E664C on LayoutPageTemplateCollection (groupId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_42B0A0C3 on LayoutPageTemplateCollection (groupId, ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_5A1F4BFC on LayoutPageTemplateCollection (groupId, parentLPTCollectionId);
create unique index IX_F074765 on LayoutPageTemplateCollection (groupId, type_, ctCollectionId, lptCollectionKey[$COLUMN_LENGTH:75$]);
create unique index IX_59C463EE on LayoutPageTemplateCollection (groupId, type_, ctCollectionId, parentLPTCollectionId, name[$COLUMN_LENGTH:75$]);
create index IX_D2A97D41 on LayoutPageTemplateCollection (groupId, type_, name[$COLUMN_LENGTH:75$]);
create index IX_A27EFF8D on LayoutPageTemplateCollection (groupId, type_, parentLPTCollectionId);
create index IX_A17F0EBD on LayoutPageTemplateCollection (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E4BCB00E on LayoutPageTemplateEntry (ctCollectionId, plid);
create index IX_D30C0FA1 on LayoutPageTemplateEntry (groupId, classNameId, classTypeKey[$COLUMN_LENGTH:75$], defaultTemplate);
create unique index IX_8B1AF2DA on LayoutPageTemplateEntry (groupId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_246C8117 on LayoutPageTemplateEntry (groupId, ctCollectionId, layoutPageTemplateEntryKey[$COLUMN_LENGTH:75$]);
create unique index IX_92540951 on LayoutPageTemplateEntry (groupId, ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_E7CC5585 on LayoutPageTemplateEntry (groupId, layoutPageTemplateCollectionId);
create index IX_FFE79984 on LayoutPageTemplateEntry (groupId, name[$COLUMN_LENGTH:75$], layoutPageTemplateCollectionId);
create index IX_416DDC6A on LayoutPageTemplateEntry (groupId, name[$COLUMN_LENGTH:75$], status, layoutPageTemplateCollectionId);
create index IX_22911887 on LayoutPageTemplateEntry (groupId, status, classNameId, classTypeKey[$COLUMN_LENGTH:75$], defaultTemplate);
create index IX_DB1B076B on LayoutPageTemplateEntry (groupId, status, layoutPageTemplateCollectionId);
create index IX_F516221F on LayoutPageTemplateEntry (groupId, type_, classNameId, classTypeKey[$COLUMN_LENGTH:75$]);
create index IX_F406284D on LayoutPageTemplateEntry (groupId, type_, classNameId, defaultTemplate);
create index IX_CD9D4A70 on LayoutPageTemplateEntry (groupId, type_, layoutPageTemplateCollectionId);
create index IX_AEC15F00 on LayoutPageTemplateEntry (groupId, type_, name[$COLUMN_LENGTH:75$], classNameId, classTypeKey[$COLUMN_LENGTH:75$]);
create unique index IX_10008A8D on LayoutPageTemplateEntry (groupId, type_, name[$COLUMN_LENGTH:75$], layoutPageTemplateCollectionId, ctCollectionId);
create index IX_67C91F5A on LayoutPageTemplateEntry (groupId, type_, name[$COLUMN_LENGTH:75$], status, classNameId, classTypeKey[$COLUMN_LENGTH:75$]);
create index IX_2EF45379 on LayoutPageTemplateEntry (groupId, type_, status, classNameId, classTypeKey[$COLUMN_LENGTH:75$]);
create index IX_9C2D0C95 on LayoutPageTemplateEntry (groupId, type_, status, defaultTemplate);
create index IX_A185457E on LayoutPageTemplateEntry (layoutPrototypeId);
create index IX_2D68D26F on LayoutPageTemplateEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_350C34AD on LayoutPageTemplateStructure (groupId, ctCollectionId, plid);
create unique index IX_545A15BA on LayoutPageTemplateStructure (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_843407A3 on LayoutPageTemplateStructureRel (layoutPageTemplateStructureId, segmentsExperienceId, ctCollectionId);
create index IX_12808938 on LayoutPageTemplateStructureRel (segmentsExperienceId);
create unique index IX_812060B7 on LayoutPageTemplateStructureRel (uuid_[$COLUMN_LENGTH:75$], ctCollectionId, groupId);