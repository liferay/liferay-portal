create index IX_F6225631 on SegmentsEntry (active_);
create index IX_175FC150 on SegmentsEntry (companyId);
create index IX_2E0C3F77 on SegmentsEntry (groupId, active_);
create unique index IX_3319A90D on SegmentsEntry (groupId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_7DDC7831 on SegmentsEntry (groupId, ctCollectionId, segmentsEntryKey[$COLUMN_LENGTH:75$]);
create index IX_1EDBDAA1 on SegmentsEntry (groupId, source[$COLUMN_LENGTH:75$]);
create unique index IX_78D59000 on SegmentsEntry (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_90AB04A7 on SegmentsEntry (source[$COLUMN_LENGTH:75$]);
create index IX_8046BADC on SegmentsEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_64CBABA8 on SegmentsEntryRel (classNameId, classPK, groupId);
create unique index IX_E418FCB9 on SegmentsEntryRel (classNameId, classPK, segmentsEntryId, ctCollectionId);
create index IX_AB286250 on SegmentsEntryRel (segmentsEntryId);

create index IX_65648B53 on SegmentsEntryRole (roleId);
create unique index IX_2876B1F2 on SegmentsEntryRole (segmentsEntryId, roleId, ctCollectionId);

create index IX_FF91202F on SegmentsExperience (groupId, active_);
create unique index IX_3C2677C5 on SegmentsExperience (groupId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_6C24C43C on SegmentsExperience (groupId, ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_EBCFE1C4 on SegmentsExperience (groupId, plid, active_);
create unique index IX_6E29AF1B on SegmentsExperience (groupId, plid, ctCollectionId, priority);
create unique index IX_1877BBA2 on SegmentsExperience (groupId, plid, ctCollectionId, segmentsExperienceKey[$COLUMN_LENGTH:75$]);
create index IX_4EA4A03D on SegmentsExperience (groupId, plid, priority);
create index IX_F35DC382 on SegmentsExperience (groupId, plid, segmentsEntryERC[$COLUMN_LENGTH:75$], segmentsEntryScopeERC[$COLUMN_LENGTH:75$], active_);
create index IX_2C43E1D2 on SegmentsExperience (groupId, segmentsEntryERC[$COLUMN_LENGTH:75$], segmentsEntryScopeERC[$COLUMN_LENGTH:75$]);
create index IX_544A601C on SegmentsExperience (segmentsEntryERC[$COLUMN_LENGTH:75$], segmentsEntryScopeERC[$COLUMN_LENGTH:75$]);
create index IX_42071D24 on SegmentsExperience (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4516B4A9 on SegmentsExperiment (groupId, ctCollectionId, segmentsExperienceId, plid);
create unique index IX_243B65ED on SegmentsExperiment (groupId, ctCollectionId, segmentsExperimentKey[$COLUMN_LENGTH:75$]);
create unique index IX_451FEC8B on SegmentsExperiment (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_127B4FCF on SegmentsExperiment (segmentsExperimentKey[$COLUMN_LENGTH:75$]);
create index IX_2701CFF1 on SegmentsExperiment (uuid_[$COLUMN_LENGTH:75$]);

create index IX_A96BB95B on SegmentsExperimentRel (segmentsExperienceId);
create unique index IX_9EDCFAE5 on SegmentsExperimentRel (segmentsExperimentId, segmentsExperienceId, ctCollectionId);