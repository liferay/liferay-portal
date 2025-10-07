create index IX_847AC537 on SamlIbSloMessage (samlIdpSessionIndex[$COLUMN_LENGTH:200$]);

create index IX_87463CFB on SamlIdpSpConnection (companyId, samlSpEntityId[$COLUMN_LENGTH:1024$]);

create index IX_545F7B35 on SamlIdpSpSession (createDate);
create index IX_8EDF9D43 on SamlIdpSpSession (samlIdpSsoSessionId);

create index IX_E5D1CDD3 on SamlIdpSsoSession (createDate);
create index IX_5E8BFDF9 on SamlIdpSsoSession (samlIdpSsoSessionKey[$COLUMN_LENGTH:75$]);
create index IX_933DA9CF on SamlIdpSsoSession (userId);

create index IX_A082E91 on SamlPeerBinding (companyId, deleted, samlNameIdValue[$COLUMN_LENGTH:1024$]);
create index IX_D9FD7C38 on SamlPeerBinding (companyId, deleted, userId, samlPeerEntityId[$COLUMN_LENGTH:1024$]);

create index IX_49073861 on SamlSpAuthRequest (createDate);
create index IX_10D77E09 on SamlSpAuthRequest (samlIdpEntityId[$COLUMN_LENGTH:1024$], samlSpAuthRequestKey[$COLUMN_LENGTH:75$]);

create index IX_61204DD on SamlSpIdpConnection (companyId, samlIdpEntityId[$COLUMN_LENGTH:1024$]);

create index IX_31762094 on SamlSpMessage (expirationDate);
create index IX_5615F9DD on SamlSpMessage (samlIdpEntityId[$COLUMN_LENGTH:1024$], samlIdpResponseKey[$COLUMN_LENGTH:75$]);

create index IX_C052F506 on SamlSpSession (companyId, sessionIndex[$COLUMN_LENGTH:200$]);
create index IX_85F532ED on SamlSpSession (jSessionId[$COLUMN_LENGTH:200$]);
create index IX_5C25BCF on SamlSpSession (samlPeerBindingId);
create unique index IX_C66E4319 on SamlSpSession (samlSpSessionKey[$COLUMN_LENGTH:75$]);