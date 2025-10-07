create index IX_B0EF9B6B on CTSChild (companyId, ctsGrandParentId);
create index IX_265AE8FB on CTSChild (companyId, parentCTSChildId);

create index IX_C89F1BAE on CTSGrandParent (companyId);

create index IX_68A07819 on CTSParent (companyId, ctsGrandParentId);