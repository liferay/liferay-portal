create index IX_F6225631 on SegmentsEntry (active_);
create index IX_2E0C3F77 on SegmentsEntry (groupId, active_);
create unique index IX_E42D8589 on SegmentsEntry (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_DB53F1B1 on SegmentsEntry (groupId, segmentsEntryKey[$COLUMN_LENGTH:75$], ctCollectionId);
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
create unique index IX_ED3A5441 on SegmentsExperience (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_EBCFE1C4 on SegmentsExperience (groupId, plid, active_);
create unique index IX_7F495C9B on SegmentsExperience (groupId, plid, priority, ctCollectionId);
create index IX_F35DC382 on SegmentsExperience (groupId, plid, segmentsEntryERC[$COLUMN_LENGTH:75$], segmentsEntryScopeERC[$COLUMN_LENGTH:75$], active_);
create unique index IX_8ED0881E on SegmentsExperience (groupId, plid, segmentsExperienceKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_2C43E1D2 on SegmentsExperience (groupId, segmentsEntryERC[$COLUMN_LENGTH:75$], segmentsEntryScopeERC[$COLUMN_LENGTH:75$]);
create unique index IX_E606CEB8 on SegmentsExperience (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_544A601C on SegmentsExperience (segmentsEntryERC[$COLUMN_LENGTH:75$], segmentsEntryScopeERC[$COLUMN_LENGTH:75$]);
create index IX_42071D24 on SegmentsExperience (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_5CB505A9 on SegmentsExperiment (groupId, segmentsExperienceId, plid, ctCollectionId);
create unique index IX_9749F869 on SegmentsExperiment (groupId, segmentsExperimentKey[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_451FEC8B on SegmentsExperiment (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_127B4FCF on SegmentsExperiment (segmentsExperimentKey[$COLUMN_LENGTH:75$]);
create index IX_2701CFF1 on SegmentsExperiment (uuid_[$COLUMN_LENGTH:75$]);

create index IX_A96BB95B on SegmentsExperimentRel (segmentsExperienceId);
create unique index IX_9EDCFAE5 on SegmentsExperimentRel (segmentsExperimentId, segmentsExperienceId, ctCollectionId);