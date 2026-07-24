## BND Definition Keys

Definition keys in *.bnd files have to match either:

* a key defined in
[BUNDLE\_SPECIFIC\_HEADERS](https://github.com/bndtools/bnd/blob/6.4.0/biz.aQute.bndlib/src/aQute/bnd/osgi/Constants.java#L320),
[headers](https://github.com/bndtools/bnd/blob/6.4.0/biz.aQute.bndlib/src/aQute/bnd/osgi/Constants.java#L79),
[options](https://github.com/bndtools/bnd/blob/6.4.0/biz.aQute.bndlib/src/aQute/bnd/osgi/Constants.java#L300)
or `-bundleannotations`

or

* a Liferay specific definition key defined in `_APP_BND_DEFINITION_KEYS`,
 `_BND_BND_DEFINITION_KEYS`, `_COMMON_BND_DEFINITION_KEYS`,
`_SUBSYSTEM_BND_DEFINITION_KEYS` and `_SUITE_BND_DEFINITION_KEYS` or
[BNDSourceUtil](https://github.com/liferay/liferay-portal/blob/master/modules/util/source-formatter/src/main/java/com/liferay/source/formatter/check/util/BNDSourceUtil.java)