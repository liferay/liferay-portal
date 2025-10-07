# Liferay Ant BND Change Log

## 3.2.16 - 2025-07-02

### Commits
- [LPD-59099] ant-bnd: switch implicit required packages on javax taglib URIs
(aaf0b33b24)
- [LPD-59099] ant-bnd: re-adds old javax strings (4d2028cf13)
- [LPD-59099] ant-bnd: adds test case (ce4d46ea16)

## 3.2.15 - 2025-05-27

### Commits
- [LPD-53852] Sort (5aa8f35954)
- [LPD-53852] Sort (87d0cea482)
- [LPD-56284] Full generation java (347e9a9017)
- [LPD-56284] Full generation jsp (a38ee5d688)
- [LPD-55089] Upgrade to latest version (d64e936946)
- [LPD-51190] Update to postgresql155 since it should be the default DB on CI
instead of mysql (da1c868811)

## 3.2.14 - 2025-01-16

### Commits
- [LPD-41335] Rename (e0d13d1ce1)
- [LPD-41335] Rename (28a0b95846)
- [LPD-41335] Add unit tests to check that the old language header for 7.x
versions (41a1b5280c)
- [LPD-41335] Add 'module.only' only when LanguageResources header is used
(39f3148860)
- [LPD-41335] Check portal version and use the old language header for 7.x
branches (61bbed7ff6)
- [LPD-41335] SF, add method to retrieve language header (350a7bd04e)

## 3.2.13 - 2024-12-27

### Commits
- [LPD-41335] Update and fix BND plugin unit tests (52aac9b40a)
- [LPD-41335] Add 'module.only=true' for backwards compatibility (236a47e4c7)
- [LPD-41335] Change BND plugins to use LanguageResources header (5d3402f35f)
- [LRCI-4023] Modify rules based on batch types (4455184027)
- [LPD-37253] Strip -jdk8 from modules.*-jdk8 test batches (0c91ba9f1e)
- [LPD-36975] liferay-portal: auto SF (b1c315ad65)
- [LPD-32570] SF, move variable declaration (9e8c7944b5)
- [LRCI-4395] Update test.properties files to use new rules engine (a1e96597fc)
- [LRCI-4420] Add additional module test.properties (ce58e967b0)

### Dependencies
- [LPD-36975] Update the ant dependency to version 1.10.14.

## 3.2.12 - 2024-06-14

### Commits
- [LPD-28218] Sort alphabetically (31a5f7b303)
- [LPS-133987] . See
https://docs.oracle.com/cd/E19316-01/819-3669/bnajs/index.html (a1ac4d7ffc)
- [LPD-26915] Adds proper testray.main.component.name (5dbfe7c9bd)

## 3.2.11 - 2024-04-10

### Commits
- [LPD-15162] Pin version (f636f23295)
- [LPD-15162] Fix JspAnalyzerPlugin ordering (d2430618cc)
- [LPD-15162] Upgrade bnd (a7dbf5877a)
- [LPS-121479] Fix test failure (bf177ad867)
- [LPS-121479] Upgrade lodash dependency version to avoid vulnerability
(40e8c0d462)

### Dependencies
- [LPD-15162] Update the aQute.libg dependency to version 6.4.0.
- [LPD-15162] Update the biz.aQute.bnd dependency to version 6.4.0.

## 3.2.10 - 2023-08-04

### Commits
- [LPS-181508] Move to workspace (c295b2db72)
- [LPS-181508] Use biz.aQute.bnd.gradle (f58467dfac)
- [LPS-181508] Update bnd (8e6ef78937)
- [LPS-181508] Auto SF (runtime) (5e7a1385d1)

### Dependencies
- [LPS-181508] Update the biz.aQute.bnd dependency to version 5.3.0.
- [LPS-181508] Update the biz.aQute.bnd.gradle dependency to version 5.3.0.
- [LPS-181508] Update the asm dependency to version 5.1.
- [LPS-181508] Update the aQute.libg dependency to version 5.3.0.
- [LPS-181508] Update the biz.aQute.bnd dependency to version 5.3.0.
- [LPS-181508] Update the biz.aQute.bnd dependency to version 4.3.0.

## 3.2.9 - 2022-05-14

### Commits
- [LPS-152789] perp next (22b35ac090)
- [LPS-152789] Add spring context when using the portal api jar (7c9a3481de)
- [LPS-150378] SF, inline (18019b6fcd)

## 3.2.8 - 2021-10-19

### Commits
- [LPS-140947] Inline (a70097214a)
- [LPS-140947] avoid downloading external dtds when parsing xml (90305c75d9)
- [LPS-105380] Remove final from parameters (a4e6ca0985)
- [LPS-105380] Revert "LPS-105380 Remove final from parameters" (b2f9eed2c2)
- [LPS-105380] Remove final from parameters (f615ff77ff)

### Dependencies
- [LPS-137126] Update the ant dependency to version 1.10.11.

## 3.2.7 - 2021-06-11

### Commits
- [LPS-133987] JspAnalyzerPlugin does not know how to parse JSPX syntax for
taglibs (58aaed5e1d)
- [LPS-119446] Move assign statement inside else-statement (d720867ee7)
- [LPS-105380] Inline (49fe57aa62)
- [LPS-105380] Rename vars, match type (1ef3ed7b76)
- [LPS-105380] Add DTD references (022b424c0c)
- [LPS-105380] Methods don't need to be static (c42da838cc)
- [LPS-108380] SF, use String.valueOf (613edb7e63)
- [LPS-105380] Rename variables (27bff1bf7d)
- [LPS-105380] Inline (e2d8267c26)
- [LPS-105380] SF, inline (4b7f2fddfe)

### Dependencies
- [LPS-129842] Update the ant dependency to version 1.10.9.

## 3.2.6 - 2020-07-09

### Commits
- [LPS-116282] HeaderReader does not have anything ever collected into its
"toBeClosed", or loading any plugins/classes from foreign jars, it does not need
to be closed explicitly. Leave it out to make code coverage complete.
(6e05087277)
- [LPS-116282] Add unit test (3bdca492df)
- [LPS-116282] Add bnd plugin to generate module portal profile component.
(413e7e4d2a)
- [LPS-115364] Update ant to 1.9.15 (2c4390c048)

### Dependencies
- [LPS-115364] Update the ant dependency to version 1.9.15.

## 3.2.5 - 2020-04-06

### Commits
- [LPS-110835] Backward compatibility (a8c32425b9)

## 3.2.4 - 2020-04-03

### Commits
- [LPS-110835] Apply everywhere (9dde521bbe)
- [LPS-110835] Match JSP compiler resource path (e022603f38)
- [LPS-110426] Use try-with-resources instead of manually closing resources in
finally statement (4ffbcd4200)
- [LPS-108328] Remove version number or SNAPSHOT check (08b8cd11ea)
- [LPS-106938] Add optional test qualifier (3129783c8a)
- [LPS-105380] Rename exception variables (b3173da81b)
- [LPS-102700] Fix bnd error (qualifiers) (688a81ff8b)
- [LPS-102700] Fix bnd error (include literal dot) (d65985bae3)

## 3.2.3 - 2019-10-03

### Commits
- [LPS-102700] Update biz.aQute.bnd.gradle to 4.3.0 (4dd7abdd9c)

### Dependencies
- [LPS-102700] Update the biz.aQute.bnd dependency to version 4.3.0.

## 3.2.2 - 2019-09-27

### Commits
- [LPS-101947] inline (9e6be0d27c)
- [LPS-101947] Use char instead of string when possible (a7816afd81)
- [LPS-101947] Fix windows path issue (9e98158b2d)

## 3.2.1 - 2019-09-17

### Commits
- [LPS-101450] Fix range schema version test (1ef0395518)
- [LPS-101450] Adapting tests (8533154d37)
- [LPS-101450] Minor schema version changes are required but compatible with
previous version of the code (daf6c5c8c5)

## 3.2.0 - 2019-08-08

### Commits
- [LPS-98937] Source formatting (a67bd0f220)
- [LPS-98937] Update instruction name (3aa405205a)

## 3.1.1 - 2019-08-08

### Commits
- [LPS-98937] add new bnd plugin that can add resources after analyzer is run
(7133543121)

## 3.1.0 - 2019-07-30

### Commits
- [LPS-98190] ant-bnd (68c493792e)
- [LPS-98190] Source formatting (b932ed61a9)
- [LPS-98190] move MetatypePlugin back to ant-bnd to support maven as well
(8c822456cc)
- [LPS-98801] [LPS-96095] auto SF for ant (bf3c0ef390)
- [LPS-84119] Pass methodcall directly instead of declaring variable first
(1e8886f5ef)
- [LPS-98198] I think this is more accurate (4ab7b5b5f8)
- [LPS-98198] Remove unused packageinfo files (2b55a617d0)

### Dependencies
- [LPS-98801 LPS-96095] Update the ant dependency to version 1.9.14.

## 3.0.8 - 2019-07-12

### Commits
- [LPS-98198] baseline (e1691fd400)
- [LPS-98198] Fix export package (4758b069d3)
- [LPS-84119] Clean up source-formatter-suppressions.xml on master (7e3ee2c759)
- [LPS-84119] Remove redundant continue statements (1a11c9772d)

## 3.0.7 - 2019-05-09

### Commits
- [LPS-95330] Remove org.jboss.shrinkwrap usage (889e82b68e)
- [LPS-95330] Always a single class in the package (37031c3e29)
- [LPS-95330] It is an optional package, not multiple packages (32b05b43fe)
- [LPS-95330] Programly add resources rather than using files. (0e608c1dfb)
- [LPS-95330] It is an optional arg, not a vararg. (113912ac33)
- [LPS-95330] Move Baseline/BaselineProcessor to where it is really used.
(6cbfce8baf)
- [LPS-95330] Remove unused classes. (c78ef822f8)
- [LPS-84119] Use toArray(new T[0]) instead of toArray(new T[size()])
(c23914c90b)

## 3.0.6 - 2019-01-08

### Commits
- [LPS-88903] avoid chaining (d8d790553c)
- [LPS-88903] I want to use ant-bnd usable with newer versions of bnd
(c7506a77d1)

## 3.0.5 - 2018-12-20

### Commits
- [LPS-88382] Revert "LPS-88382 As we provide an implementation for
http://java.sun.com/jsp/jstl/core ensure that it's added as a required
capability when used." (cacfa60ced)
- [LPS-88382] Revert "LPS-88382 As we provide an implementation for
http://java.sun.com/jsp/jstl/core it should not be blacklisted and it must be
added as a requirement." (50442eb7eb)

## 3.0.4 - 2018-12-19

### Commits
- [LPS-88382] As we provide an implementation for
http://java.sun.com/jsp/jstl/core it should not be blacklisted and it must be
added as a requirement. (0682999f5c)
- [LPS-88382] As we provide an implementation for
http://java.sun.com/jsp/jstl/core ensure that it's added as a required
capability when used. (9cac2914aa)

## 3.0.3 - 2018-11-27

### Commits
- [LPS-87839] Check when there's no difference but the packageinfo versions are
not equal (ba40d0f962)

## 3.0.2 - 2018-11-26

### Commits
- [LPS-87839] The baseline task should fail when there are mismatched package
names (4ace47f8e2)

## 3.0.1 - 2018-11-22

### Commits
- [LPS-87776] Allow the baseline task to update the Bundle-Version when all the
packageinfo files are correct (c844c0296a)
- [LPS-86806] Standardize formatting for arrays (eed36c439a)

## 2.0.60 - 2018-11-19

### Commits
- [LPS-87503] Update the Bundle-Version when there is a calculated mismatch
(31362eaa1c)
- [LPS-87503] Sort methods alphabetically (9d71e128e4)
- [LPS-87503] Simplify (no logic changes) (e597f95774)
- [LPS-87503] Rename method (no logic changes) (9f9afe2290)
- [LPS-84119] Move variable declaration inside if/else statement for better
performance (8dd499456b)

## 2.0.59 - 2018-10-16

### Commits
- [LPS-85678] ant-bnd (65a1765907)
- [LPS-85678] Search parent directories for .lfrbuild-packageinfo files
(e4cb25c4d3)

## 2.0.58 - 2018-10-16

### Commits
- [LPS-86332] ant-bnd (36fe115bcd)
- [LPS-85678] Sort (19da0679d4)
- [LPS-85678] Allow baseline task to ignore more warnings (b1f5766a57)
- [LPS-85678] Move check into isIgnoredWarnings (no logic changes) (69500dbedf)
- [LPS-85678] Use constants (no logic changes) (d2d493f00f)

## 2.0.57 - 2018-10-05

### Commits
- [LPS-80388] assert that we don't have a ":type" on our service.ranking
property (fe66992014)
- [LPS-80388] Remove incorrect type (f65b5cefe3)

## 2.0.56 - 2018-09-24

### Commits
- [LPS-85678] Allow "gradlew baseline" to ignore warnings (07e2a09cd8)

## 2.0.55 - 2018-08-30

### Commits
- [LPS-80388] use correct type for service.ranking and always leave some room
for customization (9adc812c01)

## 2.0.54 - 2018-08-23

### Commits
- [LPS-83067] Source formatting (e1cb7a5779)
- [LPS-83067] Update packageinfo when the suggested and newer versions do not
match (655c6843bf)

## 2.0.53 - 2018-07-02

### Commits
- [LPS-83067] Fix NPE (2f22e00787)

## 2.0.52 - 2018-06-28

### Commits
- [LPS-83067] Allow to ignore excessive version increases (7e3b1c8bcb)

## 2.0.51 - 2018-06-15

### Commits
- [LPS-82534] Pull out a Pattern for comment removal (0f7845431f)
- [LPS-82534] Do explicit white space trimming (022b4870de)
- [LPS-82534] The regex is actually wrong. (acaece1321)
- [LPS-79679] Fix incorrect naming (6d14ee950f)

### Dependencies
- [LPS-75049] Update the com.liferay.portal.impl dependency to version default.
- [LPS-75049] Update the ant dependency to version 1.9.4.

## 2.0.50 - 2018-04-03

### Commits
- [LPS-74110] use the new bnd for various in the build (85df7e8dff)
- [LPS-74110] baseline recommends MAJOR bundle version change when only changes
are moved packages (23d3dd8d76)

### Dependencies
- [LPS-74110] Update the biz.aQute.bnd dependency to version 3.5.0.

## 2.0.49 - 2018-04-02

### Commits
- [LPS-77425] Partial revert of d25f48516a9ad080bcbd50e228979853d3f2dda5
(60d3a950d6)
- [LPS-77425] Increment all major versions (d25f48516a)

## 2.0.48 - 2018-03-08

### Commits
- [LPS-78571] Overwrite existing packageinfo files (1049b33ca8)
- [LPS-78571] Use bnd IO util (472f719887)
- [LPS-78571] Use try-with-resources (b3ccbb7c23)
- [LPS-78459] Remove old suppressions files (6a22708df9)
- [LPS-78459] Add new source-formatter-suppressions.xml files (7c99bebf61)

## 2.0.47 - 2018-03-07

### Commits
- [LPS-78571] Fix Baseline to properly support packageinfo from compat jars
(f7c39285c2)

## 2.0.46 - 2018-03-05

### Commits
- [LPS-76997] Change ServiceAnalyzerPlugin to read version from spring extender
api (8d3a3a5f2f)
- [LPS-76926] Fixing tests (d61622af14)

## 2.0.45 - 2018-03-01

### Commits
- [LPS-76926] Apply backward compatibility to SDK (cf264bf95a)

## 2.0.44 - 2018-02-26

### Commits
- [LPS-76926] Not a StringBundler (6a93779f53)
- [LPS-76926] Fixing the tests and a new one for range of requireSchemaVersions
(598797e531)
- [LPS-76926] Allow compatibility with micro versions of requireSchemaVersion.
Also allow to define a range of requireSchemaVersions. Filter to state OK to do
not activate the module when an upgrade is in progress (1a3d8ded04)
- [LPS-76926] Use bnd to support ranges? (f6c5058bae)
- [LPS-76926] Add the ability of reverting micro changes in schemaVersion. Micro
changes are reserved for those upgrade processes where the data is modified but
not the logic. Be able to revert means to come back to the previous version of
the module which also works with the new data. SimpleFilter in
org.apache.felix.framework.capabilityset does not support neither range filter
nor < operator (4898547edc)

## 2.0.43 - 2018-02-01

### Commits
- [LPS-77350] ant bnd (fd103fcae0)
- [LPS-77350] where there's a 2, there's a 1 (5b12602682)
- [LPS-77350] Avoid resolving external DTDs when reading service.xml
(8d10e0290d)
- [LPS-77350] Sort the class names to make the manifest more stable (190f5cbfcb)
- [LPS-77350] Reuse XML utils (ab3f6daf95)
- [LPS-77350] for osgi modules automatically include service.xml into jar
(dab496bebb)
- [LPS-77350] scan jar for service.xml files and emit the services as OSGi
capabilities (2ce91e5fd0)

## 2.0.42 - 2017-12-01

### Commits
- [LPS-76224] Typo (73314f7549)
- [LPS-76224] don't add package imports for page imports that are commented out
(17b4162b62)
- [LPS-76227] Ignore JavaXMLSecurityCheck (b1277660ec)
- [LPS-76227] Add missing parentheses (4cb26ef5e2)
- [LPS-76227] Add missing line breaks (9dfc8ef381)

## 2.0.41 - 2017-10-04

### Commits
- [LPS-74110] Revert "LPS-74110 baseline recommends MAJOR bundle version change
when only changes are moved packages" (06f33ace90)
- [LPS-74110] Revert "LPS-74110 use the new bnd for various in the build"
(3df5dbe6f2)
- [LPS-74110] Automatically update dependencies.properties when publishing
(d0a5542bee)
- [LPS-74110] use the new bnd for various in the build (0a064baade)
- [LPS-74110] baseline recommends MAJOR bundle version change when only changes
are moved packages (30a4d749c1)
- [LPS-73481] Creating DTDs for 7.1.0 and apply them in XML files. (15f47d7471)

### Dependencies
- [LPS-74110] Update the biz.aQute.bnd dependency to version 3.2.0.
- [LPS-74110] Update the biz.aQute.bnd dependency to version 3.5.0.

## 2.0.40 - 2017-09-11

### Commits
- [LPS-73481] Ant-bnd semantic versioning: take all dtds from portal-impl
(f2ad592d3d)

### Dependencies
- [LPS-73481] Update the com.liferay.portal.impl dependency to version default.

## 2.0.37 - 2017-04-28

### Commits
- [LPS-71728] Use new constant everywhere (ec8ebfc50e)
- [LPS-71728] restore logic when no -aggregate instruction is present
(580e4ab3e5)
- [LPS-71728] rename (2832566eb5)

## 2.0.36 - 2017-04-27

### Commits
- [LPS-71728] merge headers instead of overwriting them (336580e17a)
- [LPS-71728] sort delegate plugins and update logic to support new order
(27cbd21076)
- [LPS-71728] Web-ContextPath is not a real path in the file system (ecee284c43)
- [LPS-71728] Typo (01d3b49b65)
- [LPS-71728] Sort (5e6377765c)
- [LPS-71729] No need to sort the keys twice (97cb0028ab)
- [LPS-71728] Split code in two methods (9f0052831b)
- [LPS-71728] Extract constant (598ab1a0fd)
- [LPS-71728] Avoid chaining (b802190b60)
- [LPS-71728] Separate words like in "-liferay-spring-dependency" (6a99706b9e)
- [LPS-71728] Since it's a Liferay-only directive, prefix it with "-liferay"
(f8edf08285)
- [LPS-71728] delegate from existing plugin class to two new classes
(77003bb5c2)
- [LPS-71728] Split the logic into different classes (4f3f263799)
- [LPS-71728] add support for new -aggregateresourcebundles bnd instruction to
ResourceBundleLoaderAnalyzerPlugin (dfc1f8045f)

## 2.0.35 - 2017-04-08

### Commits
- [LPS-64098] Rename (568d4d9c1e)
- [LPS-64098] Rename (67bfd67e54)
- [LPS-64098] Rename (c453f750e7)
- [LPS-64098] BundleException thrown by
OSGiManifestBuilderFactory.checkImportExportSyntax() when a JSP page directive
contains certain sequences of comma-delimited imports (4e3a1e4e97)

## 2.0.34 - 2017-03-15

### Commits
- [LPS-71118] increment for the future (1776cb2ad4)
- [LPS-71118] as used (62139716e0)
- [LPS-71118] we haven't released 2.0.34 yet (e274bc3d2d)
- [LPS-71118] update tests (6623c6d716)
- [LPS-71118] add caching to jspAnalazyerPlugin to avoid continually looking up
the same tag lig requirements (6fb16379e2)

## 2.0.33 - 2017-01-31

### Commits
- [LPS-70379] Add option to force packageinfo on 3rd-party embedded packages
(1d77c7a8aa)

## 2.0.32 - 2017-01-04

### Commits
- [LPS-69899] Add option to merge the compat jar before baselining (91f8123a60)

## 2.0.31 - 2016-11-30

### Commits
- [LPS-69470] Add option to force a calculated Bundle-Version (c66e7c24db)
- [LRQA-28693] These are only used from Ant, so the dependency is "provided"
(91dbbd33d4)

### Dependencies
- [LRQA-28693] Update the ant dependency to version 1.9.4.

## 2.0.30 - 2016-10-20

### Commits
- [LPS-67434] Do not cause a major increase if the package was only moved
(38d95ac224)

## 2.0.29 - 2016-09-07

### Commits
- [LPS-68014] Add option (enabled by default) to force 1.0.0 on new packages
(30b9a97098)

## 2.0.28 - 2016-08-04

### Commits
- [LPS-67434] Cause major increase if a package is removed (36ac8d461a)

## 2.0.27 - 2016-06-27

### Commits
- [LPS-66733] Use ByteArrayStreams to avoid bnd's thread creation upon opening
input stream on resource (6f8d3e0382)
- [LPS-66733] Remove dependency on Java 6 (50e0b75958)
- [LRQA-25824] Fix condition to hide the package diff (56f58378e2)

## 2.0.26 - 2016-06-18

### Commits
- [LRQA-25824] Fail baseline if packageinfo for a new package is missing, or if
packageinfo for a removed package can still be found (68740d4be2)
- [LRQA-25824] Remove unused argument (46727dfe0c)
- [LRQA-25824] Create the dir only if we're generating the packageinfo
(b08bfd45fc)
- [LRQA-25824] Generate a report when a packageinfo file is added (430620c91d)

## 2.0.25 - 2016-06-01

### Commits
- [LPS-66268] Fail the build with excessive version increase (7299462756)

### Dependencies
- [] Update the com.liferay.portal.impl dependency to version 2.4.0.

## 2.0.24 - 2016-05-28

### Commits
- [LPS-61748] Revert "LPS-61748 fix bug caused by bnd 3.1 regression"
(18e7846c9d)
- [LPS-66064] Revert "LPS-66064 Temp revert to "-liferay-includeresource" for
publishing" (3bb9df3e3d)

## 2.0.23 - 2016-05-24

### Commits
- [LPS-66064] Temp revert to "-liferay-includeresource" for publishing
(c4236d5b60)
- [LPS-66064] Fix test compile (8a08c8fed6)
- [LPS-66064] Better glob to distinguish asm-*.jar from asm-commons-*.jar
(d50ced317d)
- [LPS-66064] Remove use of "-liferay-includeresource" (bbd6b63415)
- [LPS-66064] Use Bnd 3.2.0 (512c51c100)

### Dependencies
- [LPS-66064] Update the biz.aQute.bnd dependency to version 3.2.0.

## 2.0.22 - 2016-05-11

### Commits
- [LPS-65788] If "persist", update bnd.bnd to the suggested bundle version
(b72d8cf832)
- [LPS-65383] Typo caused by 0de90c6190b8ac1f386b9829e091b564915d206b
(adebf81bfd)

## 2.0.21 - 2016-05-10

### Commits
- [LPS-65383] Not needed (0f6acfbb58)
- [LPS-65383] Consistency with the other tests (3bfb7dc78c)
- [LPS-65383] formatSource (c26196337d)
- [LPS-65383] fix typo (b096a97245)
- [LPS-65383] Remove duplicate taglib requirements and Add Test for removing
duplicate method (4efb27ede0)

## 2.0.20 - 2016-04-22

### Commits
- [LPS-65283] Resolve using embedded DTD if found (eef966e178)
- [LPS-65283] Embed the liferay-social DTDs (cb94c10e82)
- [LPS-61099] Delete build.xml in modules (c9a7e1d370)

### Dependencies
- [LPS-65283] Update the com.liferay.portal.impl dependency to version default.

## 2.0.19 - 2016-03-29

### Commits
- [LPS-63162] Import packages used in liferay-social.xml (848bb36bc5)

## 2.0.18 - 2016-03-17

### Commits
- [LPS-64092] this is the default value (6eb199a902)
- [LPS-64092] fix issue with whitespace that trim doesn't handle properly
(0a2d346a42)

## 2.0.17 - 2016-03-15

### Commits
- [LPS-64191] Do not exit if source dir does not exist, so it can create
"src/main/resources" if needed (24963eeced)

## 2.0.16 - 2016-03-15

### Commits
- [LPS-64191] Extract baseline code into class (3a30664023)
- [LPS-64191] Typo (2f515e30e7)

## 2.0.15 - 2016-03-11

### Commits
- [LPS-64182] fix packageinfo creation on deleted packages (ff0736a6ce)
- [LPS-64182] fix bug in baseline task with file references (38aaeff31a)

## 2.0.14 - 2016-03-11

### Commits
- [LPS-63855] Avoid downcasting (0a0d98a8c4)
- [LPS-63855] Don't require tlds that you provide (5632dde6f8)

## 2.0.13 - 2016-03-09

### Commits
- [LPS-63462] Also add symbolic name to the capability (554005ceb9)

## 2.0.12 - 2016-03-08

### Commits
- [LPS-64029] Bnd plugin to add missing SB instructions (094f3cedc3)
- [LPS-63943] This is done automatically now (f1e42382d9)

## 2.0.11 - 2016-03-02

### Commits
- [LPS-63462] Rename, to match "servlet.context.name" (133a16b47c)

## 2.0.10 - 2016-03-02

### Commits
- [LPS-63462] rename (83210770f8)
- [LPS-63462] Add bnd plugin to set capabilities for regular bundles
(0c380da3d9)
- [LPS-58478] Forgot file (cbad8d8edf)
- [LPS-58478] Sort (7f6e3af6c6)
- [LPS-58478] SF rename jps files (5708708a96)
- [LPS-58478] add unit test (f5fbc36ab8)
- [LPS-58478] strip all comments out of JSP content before processing taglib
uris (cb951c6480)
- [LPS-62883] Update gradle-plugins/build.gradle (20fc2457e6)

## 2.0.9 - 2016-02-08

### Commits
- [LPS-61952] Update java usages (f855d62e1e)

## 2.0.8 - 2016-02-04

### Commits
- [LPS-62571] Rename "-spring-dependency" in Java files (f0c47fa37a)

## 2.0.7 - 2016-02-04

### Commits
- [LPS-62571] Rename "Require-SchemaVersion" in Java files (e220a0e456)

## 2.0.6 - 2016-01-28

### Commits
- [LPS-62384] Typo (7e5530056e)
- [LPS-62384] Fix tests (59af8ab3b8)
- [LPS-62384] Never thrown (0b97cf284b)
- [LPS-62384] As used (078947db5c)
- [LPS-62384] Fix compile (12290dee44)
- [LPS-62384] Update test (58d3d0dbf5)
- [LPS-62384] Move to NpmAnalyzerPluginTest (No logical changes) (36602650f1)
- [LPS-62384] Switch to npm modules and package.json instead of bower.json
(15d2bbfb4a)
- [LPS-62384] Move to NpmAnalyzerPlugin (No logical changes) (75842a24e1)
- [LPS-61088] Remove classes and resources dir from Include-Resource
(1b0e1275bc)

## 2.0.5 - 2016-01-09

### Commits
- [LPS-61748] fix bug caused by bnd 3.1 regression (1786ac45f0)
- [LPS-61748] upgrade deps for bnd 3.1 (901a30116a)
- [LPS-55691] Deploy directly to lib/development (7a5801f7a3)
- [LPS-61420] Fix incorrect line breaks (b2815278b2)

### Dependencies
- [LPS-61748] Update the biz.aQute.bnd dependency to version 3.1.0.

## 2.0.3 - 2015-10-19

### Commits
- [LPS-59564] Fix tests on Windows (0a2b466965)
- [LPS-59564] Update directory layout for "sdk" modules (ea19635556)
- [LPS-59279] Use the Bnd symbolic name (2705ee0dd3)

## 2.0.2 - 2015-09-11

### Commits
- [LPS-56986] Avoid adding an empty line to context.dependencies (fdc65ea4f3)
- [LPS-56986] Avoid creating an empty context.dependencies file (e3c265a7ea)
- [LPS-56986] Update the tests (b2998ff809)
- [LPS-56986] Let's publish services of type Release. We cannot couple the
plugin to the portal, so we are using the classname (79e58fd540)

## 2.0.1 - 2015-09-09

### Commits
- [LPS-56986] Sort (5554143c0a)
- [LPS-56986] Simplify (baa8fdefcb)
- [LPS-56986] Sort (623e1a789e)
- [LPS-56986] Rename, according to Jorge's advice (56120821dd)
- [LPS-56986] Process the filterString so we can define filters in the Spring
contexts (a2758146a9)
- [LPS-51081] Remove modules' Eclipse project files (b3f19f9012)
- [LPS-51081] Replace modules' Ant files with Gradle alternatives (9e60160a85)
- [LPS-51081] Remove modules' Ivy files (076b384eef)
- [LPS-51081] Simplify (47109bb8b7)
- [LPS-51081] Ran "ant reset-gradle init-gradle" and sync projects list
(0a322f9432)
- [LPS-56986] Inconsistent renaming, caused by
3e67d52e8cb1b35ed25616e0558e29528a87b646 (9f1129be67)

### Dependencies
- [LPS-51081] Update the shrinkwrap-depchain dependency to version 1.2.2.

## 1.0.11 - 2015-07-09

### Commits
- [LPS-56986] since we're breaking the API (2f6bf02506)
- [LPS-56986] Rename to -spring-dependency to match jar name. If you like the
other flag more, then let's rename the class (3e67d52e8c)
- [LPS-56986] Simplify/SF (d4e12699f1)
- [LPS-56986] Rename packages for consistency (34125a8723)
- [LPS-56986] regen (7ba6abd2f5)
- [LPS-56986] Unit tests for the plugin (2a02b1e01b)
- [LPS-56986] Bnd plugin to define dependencies for an Spring Application
context (93f845ce84)
- [LPS-51081] Remove the child copySpec approach in the "deploy" task, so it
will be easier to add extra files ("osgi.runtime.dependencies") as "from"
(25c340d8be)
- [LPS-51081] Ant to Gradle (6e2ea34e39)
- [LPS-51081] Ran "ant reset-gradle init-gradle" (9ab363b842)

### Dependencies
- [LPS-51081] Update the biz.aQute.bnd dependency to version 2.4.1.
- [LPS-51081] Update the ant dependency to version 1.9.4.

## 1.0.10 - 2015-06-08

### Commits
- [LPS-56111] Match PortletConstantsTest (abe0040d19)
- [LPS-56111] caret, not carrot (3df4ebbaed)
- [LPS-56111] Match PortletConstantsTest (2757e87566)
- [LPS-56111] Sort (973a4304af)
- [LPS-56111] only used once (66c37515f8)
- [LPS-56111] Pattern objects are not simple constants like String or int
(2a62437a4a)
- [LPS-56111] make it private (9899e892e2)
- [LPS-56111] never used? (4196ab6013)
- [LPS-56111] never used? (0f8bd387c3)
- [LPS-56111] never used? (a3f10f3f3f)
- [LPS-56111] Rename (aa31e31f56)
- [LPS-56111] forgot this (95ba29bc59)
- [LPS-56111] rename (64ca982f49)
- [LPS-56111] make protected (410c372979)
- [LPS-56111] I think this is one word (63aae7bbec)
- [LPS-56111] Rename (3ec8836bb7)
- [LPS-56111] bnd plugin tests (8aab9b5a67)
- [LPS-56111] bnd plugin (d72fff8a99)
- [LPS-55937] Prevent from future mistakes (d6445f0909)
- [LPS-55691] update everyone to 1.9.4 (d5a5b84ff9)
- [LPS-55220] Guess we sort it the other way around (7314726287)
- [LPS-55220] Use conf mapping instead of transitive attribute (42bfd15e29)

## 1.0.9 - 2015-03-24

### Commits
- [LPS-54506] up to next version (1a2e95ec30)
- [LPS-54506] minor rename and sorting of params for consistency (1f5f2da04c)
- [LPS-54506] this simplifies the API without really affecting performance. I
checked that analyzer.getReferred() just returns a reference (089a92809e)
- [LPS-54506] Rename (3141168db2)
- [LPS-54506] auto S F (af5dbbf177)
- [LPS-54506] rename (2e23974013)
- [LPS-54506] buy space (1ac76ce9ce)
- [LPS-54506] do deeper analysis of JSP imports to improve the accuracy of
package imports (b8d2ddaac3)

## 1.0.8 - 2015-03-21

### Commits
- [LPS-54434] prepare next version (48e06e0505)
- [LPS-54434] Navigating to Site Admin > Content throws
java.lang.IllegalArgumentException and wiki and bookmark portlets are
unavailable (c3288b4c99)

## 1.0.7 - 2015-03-17

### Commits
- [LPS-54299] prepare for next 1.0.8 (4acf24d7f4)
- [LPS-54299] create a bnd plugin for analyzing JSP files in web fragments and
WABs (f8b643392d)

## 1.0.6 - 2015-03-16

### Commits
- [LPS-54293] prep for 1.0.7 (3fb2138557)
- [LPS-54293] create a bnd plugin to simplify merging of the .sass-cache files
over originals in osgi bundles (b5f1a7c20c)

## 1.0.5 - 2015-03-04

### Commits
- [LPS-53927] prepare for next revision (fa57a3a135)
- [LPS-53927] Consistency of bndFile, oldJar, vs. newerJar (d86e72eec3)
- [LPS-53927] I'm going to use the @generated tag to essentially ignore it until
Ray gets it to bnd upstream (056341f641)

## 1.0.4 - 2015-03-04

### Commits
- [LPS-53927] update version (691e5321e8)
- [LPS-53927] simplify base class (d7ca3dd951)
- [LPS-53927] re-implement task around new deployer (7fab71236e)
- [LPS-53927] new simplified deployer class (81366fe99e)
- [LPS-53927] re-implement baseline jar task around new processor (36e210130a)
- [LPS-53927] simpified baseline processor (a20afba99a)
- [LPS-53927] remove the test task - unused (12c56215b7)
- [LPS-53855] Regen (6a40772ef2)
- [LPS-53855] Revert "LPS-53855 Regen" (247072ec15)
- [LPS-53855] now we up it to 1.0.5 (8f89624ab3)
- [LPS-53855] Update com.liferay.ant.bnd to bnd 2.4.1 (f7ed2a48b6)
- [LPS-52782] regenerate ant setup-eclipse (24141cdd54)
- [LPS-52737] Revert "LPS-52737 Use StringUtil.toLowerCase method, as SF tool
requires it" (7f408302c2)
- [LPS-52737] Use StringUtil.toLowerCase method, as SF tool requires it
(1fb1317275)

## 1.0.3 - 2014-12-19

### Commits
- [LPS-52289] bump the version (4d4fc86644)
- [LPS-52289] As a developer I want package versions to be generated when
possible during a baseline check (006a11fd2c)
- [LPS-52288] As a developer I want the suggested bundle version to be persisted
when baseline.jar.report.level=persist (78c3140369)

## 1.0.2 - 2014-12-05

### Commits
- [LPS-52032] released 1.0.2, so prep for 1.0.3 snapshot (573a9e3a99)
- [LPS-52032] Use the proper path depending on where we are (34cbf5f7cc)

## 1.0.1 - 2014-12-03

### Commits
- [LPS-50049] Revert "LPS-50049 Refactor SDK module" (3a17353b1b)

[LPD-15162]: https://issues.liferay.com/browse/LPD-15162
[LPD-26915]: https://issues.liferay.com/browse/LPD-26915
[LPD-28218]: https://issues.liferay.com/browse/LPD-28218
[LPD-32570]: https://issues.liferay.com/browse/LPD-32570
[LPD-36975]: https://issues.liferay.com/browse/LPD-36975
[LPD-37253]: https://issues.liferay.com/browse/LPD-37253
[LPD-41335]: https://issues.liferay.com/browse/LPD-41335
[LPD-51190]: https://issues.liferay.com/browse/LPD-51190
[LPD-53852]: https://issues.liferay.com/browse/LPD-53852
[LPD-55089]: https://issues.liferay.com/browse/LPD-55089
[LPD-56284]: https://issues.liferay.com/browse/LPD-56284
[LPD-59099]: https://issues.liferay.com/browse/LPD-59099
[LPS-50049]: https://issues.liferay.com/browse/LPS-50049
[LPS-51081]: https://issues.liferay.com/browse/LPS-51081
[LPS-52032]: https://issues.liferay.com/browse/LPS-52032
[LPS-52288]: https://issues.liferay.com/browse/LPS-52288
[LPS-52289]: https://issues.liferay.com/browse/LPS-52289
[LPS-52737]: https://issues.liferay.com/browse/LPS-52737
[LPS-52782]: https://issues.liferay.com/browse/LPS-52782
[LPS-53855]: https://issues.liferay.com/browse/LPS-53855
[LPS-53927]: https://issues.liferay.com/browse/LPS-53927
[LPS-54293]: https://issues.liferay.com/browse/LPS-54293
[LPS-54299]: https://issues.liferay.com/browse/LPS-54299
[LPS-54434]: https://issues.liferay.com/browse/LPS-54434
[LPS-54506]: https://issues.liferay.com/browse/LPS-54506
[LPS-55220]: https://issues.liferay.com/browse/LPS-55220
[LPS-55691]: https://issues.liferay.com/browse/LPS-55691
[LPS-55937]: https://issues.liferay.com/browse/LPS-55937
[LPS-56111]: https://issues.liferay.com/browse/LPS-56111
[LPS-56986]: https://issues.liferay.com/browse/LPS-56986
[LPS-58478]: https://issues.liferay.com/browse/LPS-58478
[LPS-59279]: https://issues.liferay.com/browse/LPS-59279
[LPS-59564]: https://issues.liferay.com/browse/LPS-59564
[LPS-61088]: https://issues.liferay.com/browse/LPS-61088
[LPS-61099]: https://issues.liferay.com/browse/LPS-61099
[LPS-61420]: https://issues.liferay.com/browse/LPS-61420
[LPS-61748]: https://issues.liferay.com/browse/LPS-61748
[LPS-61952]: https://issues.liferay.com/browse/LPS-61952
[LPS-62384]: https://issues.liferay.com/browse/LPS-62384
[LPS-62571]: https://issues.liferay.com/browse/LPS-62571
[LPS-62883]: https://issues.liferay.com/browse/LPS-62883
[LPS-63162]: https://issues.liferay.com/browse/LPS-63162
[LPS-63462]: https://issues.liferay.com/browse/LPS-63462
[LPS-63855]: https://issues.liferay.com/browse/LPS-63855
[LPS-63943]: https://issues.liferay.com/browse/LPS-63943
[LPS-64029]: https://issues.liferay.com/browse/LPS-64029
[LPS-64092]: https://issues.liferay.com/browse/LPS-64092
[LPS-64098]: https://issues.liferay.com/browse/LPS-64098
[LPS-64182]: https://issues.liferay.com/browse/LPS-64182
[LPS-64191]: https://issues.liferay.com/browse/LPS-64191
[LPS-65283]: https://issues.liferay.com/browse/LPS-65283
[LPS-65383]: https://issues.liferay.com/browse/LPS-65383
[LPS-65788]: https://issues.liferay.com/browse/LPS-65788
[LPS-66064]: https://issues.liferay.com/browse/LPS-66064
[LPS-66268]: https://issues.liferay.com/browse/LPS-66268
[LPS-66733]: https://issues.liferay.com/browse/LPS-66733
[LPS-67434]: https://issues.liferay.com/browse/LPS-67434
[LPS-68014]: https://issues.liferay.com/browse/LPS-68014
[LPS-69470]: https://issues.liferay.com/browse/LPS-69470
[LPS-69899]: https://issues.liferay.com/browse/LPS-69899
[LPS-70379]: https://issues.liferay.com/browse/LPS-70379
[LPS-71118]: https://issues.liferay.com/browse/LPS-71118
[LPS-71728]: https://issues.liferay.com/browse/LPS-71728
[LPS-71729]: https://issues.liferay.com/browse/LPS-71729
[LPS-73481]: https://issues.liferay.com/browse/LPS-73481
[LPS-74110]: https://issues.liferay.com/browse/LPS-74110
[LPS-75049]: https://issues.liferay.com/browse/LPS-75049
[LPS-76224]: https://issues.liferay.com/browse/LPS-76224
[LPS-76227]: https://issues.liferay.com/browse/LPS-76227
[LPS-76926]: https://issues.liferay.com/browse/LPS-76926
[LPS-76997]: https://issues.liferay.com/browse/LPS-76997
[LPS-77350]: https://issues.liferay.com/browse/LPS-77350
[LPS-77425]: https://issues.liferay.com/browse/LPS-77425
[LPS-78459]: https://issues.liferay.com/browse/LPS-78459
[LPS-78571]: https://issues.liferay.com/browse/LPS-78571
[LPS-79679]: https://issues.liferay.com/browse/LPS-79679
[LPS-80388]: https://issues.liferay.com/browse/LPS-80388
[LPS-82534]: https://issues.liferay.com/browse/LPS-82534
[LPS-83067]: https://issues.liferay.com/browse/LPS-83067
[LPS-84119]: https://issues.liferay.com/browse/LPS-84119
[LPS-85678]: https://issues.liferay.com/browse/LPS-85678
[LPS-86332]: https://issues.liferay.com/browse/LPS-86332
[LPS-86806]: https://issues.liferay.com/browse/LPS-86806
[LPS-87503]: https://issues.liferay.com/browse/LPS-87503
[LPS-87776]: https://issues.liferay.com/browse/LPS-87776
[LPS-87839]: https://issues.liferay.com/browse/LPS-87839
[LPS-88382]: https://issues.liferay.com/browse/LPS-88382
[LPS-88903]: https://issues.liferay.com/browse/LPS-88903
[LPS-95330]: https://issues.liferay.com/browse/LPS-95330
[LPS-96095]: https://issues.liferay.com/browse/LPS-96095
[LPS-98190]: https://issues.liferay.com/browse/LPS-98190
[LPS-98198]: https://issues.liferay.com/browse/LPS-98198
[LPS-98801]: https://issues.liferay.com/browse/LPS-98801
[LPS-98937]: https://issues.liferay.com/browse/LPS-98937
[LPS-101450]: https://issues.liferay.com/browse/LPS-101450
[LPS-101947]: https://issues.liferay.com/browse/LPS-101947
[LPS-102700]: https://issues.liferay.com/browse/LPS-102700
[LPS-105380]: https://issues.liferay.com/browse/LPS-105380
[LPS-106938]: https://issues.liferay.com/browse/LPS-106938
[LPS-108328]: https://issues.liferay.com/browse/LPS-108328
[LPS-108380]: https://issues.liferay.com/browse/LPS-108380
[LPS-110426]: https://issues.liferay.com/browse/LPS-110426
[LPS-110835]: https://issues.liferay.com/browse/LPS-110835
[LPS-115364]: https://issues.liferay.com/browse/LPS-115364
[LPS-116282]: https://issues.liferay.com/browse/LPS-116282
[LPS-119446]: https://issues.liferay.com/browse/LPS-119446
[LPS-121479]: https://issues.liferay.com/browse/LPS-121479
[LPS-129842]: https://issues.liferay.com/browse/LPS-129842
[LPS-133987]: https://issues.liferay.com/browse/LPS-133987
[LPS-137126]: https://issues.liferay.com/browse/LPS-137126
[LPS-140947]: https://issues.liferay.com/browse/LPS-140947
[LPS-150378]: https://issues.liferay.com/browse/LPS-150378
[LPS-152789]: https://issues.liferay.com/browse/LPS-152789
[LPS-181508]: https://issues.liferay.com/browse/LPS-181508
[LRCI-4023]: https://issues.liferay.com/browse/LRCI-4023
[LRCI-4395]: https://issues.liferay.com/browse/LRCI-4395
[LRCI-4420]: https://issues.liferay.com/browse/LRCI-4420
[LRQA-25824]: https://issues.liferay.com/browse/LRQA-25824
[LRQA-28693]: https://issues.liferay.com/browse/LRQA-28693