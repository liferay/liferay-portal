create unique index IX_DA39AA7F on CommerceTaxFixedRate (CPTaxCategoryId, commerceTaxMethodId);
create index IX_52767DD2 on CommerceTaxFixedRate (commerceTaxMethodId);

create index IX_37AE3A58 on CommerceTaxFixedRateAddressRel (CPTaxCategoryId);
create index IX_CB69750D on CommerceTaxFixedRateAddressRel (commerceTaxMethodId);