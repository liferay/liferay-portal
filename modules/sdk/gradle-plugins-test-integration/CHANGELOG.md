# Liferay Gradle Plugins Test Integration Change Log

## 4.0.6 - 2024-10-01

### Commits
- [LPD-37353] Configure testIntegration (702e947637)
- [LPD-31864] update tomcat to 9.0.90 (03fac7f67b)
- [LPD-15162] Pin version (f636f23295)
- [LPD-21549] Update Tomcat to 9.0.87, forgot one file (1c15a18d94)
- [LPD-15162] Fix tests (939fd44678)
- [LPD-15162] Move tests (f23b048eff)
- [LPD-15162] Update plugins Gradle version (7757c65773)

## 4.0.5 - 2024-03-15

### Commits
- [LPD-15162] Test (9ed390cf6f)
- [LPS-202894] Update Tomcat to 9.0.83 (491a001a1b)
- [LPS-199260] Update Tomcat to 9.0.82 (75798531f5)
- [LRQA-82692] Update Tomcat to 9.0.80 (8285ed2ec8)

### Dependencies
- [LPD-15162] Update the com.liferay.gradle.util dependency to version 1.0.49.

## 4.0.3 - 2023-08-16

### Commits
- [LPS-193415] gradle-plugins-test-integration: auto SF (ede2b74188)
- [LPS-193415] fix test integration gradle pluin error for idea (698177565d)

## 4.0.2 - 2023-08-04

### Commits
- [LPS-181508] Download (05b5afa588)
- [LPS-181508] Update plugins Gradle version (60571c128e)
- [LPS-181508] Annotations (beddb5d22a)
- [LPS-181508] Versions (0362751ca7)
- [LPS-181508] Copy (f6124575e5)
- [LPS-181508] Auto SF (runtime) (5e7a1385d1)
- [LPS-181508] Runtime (203d66906c)
- [LPS-181508] Test integration (0e28946154)
- [LPS-181508] Apply java-library (d3c0c00f02)

### Dependencies
- [LPS-181508] Update the com.liferay.gradle.util dependency to version 1.0.48.
- [LPS-181508] Update the com.liferay.gradle.util dependency to version 1.0.47.
- [LPS-181508] Update the zt-exec dependency to version 1.9.

## 4.0.1 - 2023-06-12

### Commits
- [LPS-187234] Match more (669edbae6b)
- [LPS-187234] SetUpTestableTomcatTask should match "ant -f build-test.xml
setup-testable-tomcat" (55cf76b198)
- [LPS-185311] Upgrade Tomcat to 9.0.75 (d8804a9755)
- [LPS-179403] Update Tomcat version to 9.0.73 (e930025014)
- [LPS-172797] Update Tomcat to 9.0.71 (cf9c74f03c)
- [LPS-169038] Update Tomcat to 9.0.68 for CVE-2022-42252 (552e2aa302)
- [LPS-164410] Update Tomcat to 9.0.65 for CVE-2021-43980 CVE-2022-23181
CVE-2022-29885 CVE-2022-34305 (1a9c3b614b)
- [LPS-150857] Update plugins Gradle version (4c389b37ce)

## 4.0.0 - 2022-07-15

### Dependencies
- [LPS-51081] Update the com.liferay.gradle.util dependency to version 1.0.47.

## 3.0.6 - 2022-07-15

### Commits
- [LPS-51081] Apply (52ec5f96e8)
- [LPS-51081] Rename package (9d145e2f47)
- [LPS-150378] SF, inline (15a4ebdc1f)
- [LPS-143837] [CVE-2021] -42340 in Apache Tomcat (e3ffd8abbf)
- [LPS-135481] Update tomcat version to 9.0.53 (93b1d8845d)
- [LPS-138183] Do not set liferay.mode in gradle (49355b8ba4)
- [LPS-105380] Remove final from parameters (a4e6ca0985)
- [LPS-105380] Remove final from variables (5a4e0b26b4)
- [LPS-105380] Revert "LPS-105380 Remove final from variables" (cdda76fc8e)
- [LPS-105380] Revert "LPS-105380 Remove final from parameters" (b2f9eed2c2)
- [LPS-105380] Remove final from parameters (f615ff77ff)
- [LPS-105380] Remove final from variables (ffe6186e71)
- [LPS-105380] Increase performance by moving variable declaration after throw
statement (444d3bb171)
- [LPS-130505] Revert "LPS-130505 SF, no need to call methods" (0cdafd184b)
- [LPS-130505] SF, no need to call methods (b9055a7fc9)

### Dependencies
- [LRCI-2670] Update the com.liferay.gradle.util dependency to version 1.0.46.
- [LPS-143280] Update the com.liferay.gradle.util dependency to version 1.0.45.

## 3.0.5 - 2021-04-02

### Commits
- [LPS-129995] Faster (0c567fe723)
- [LPS-129995] The startTestableTomcat task times out for non English locales
(109b1ccf03)
- [LPS-129193] Update Tomcat version to 9.0.43 (bae9cf6a26)
- [LPS-125871] Upgrade tomcat version from 9.0.37 to 9.0.40 due to
CVE-2021-24122 CVE-2020-17527 (dc8a7e4a7e)

## 3.0.4 - 2020-11-02

### Commits
- [LPS-111291] Import statements (d414bad0fa)
- [LPS-111291] Gradle 5.6.4 tests (40f4f9e2f3)
- [LPS-111291] Update plugins Gradle version (003c3832b0)
- [LPS-105380] Methods don't need to be static (c42da838cc)
- [LPS-114024] Update gradle (88a240747b)

### Dependencies
- [LPS-111291] Update the com.liferay.gradle.util dependency to version 1.0.44.
- [LPS-115020] Update the com.liferay.gradle.util dependency to version 1.0.43.

## 3.0.3 - 2020-06-02

### Commits
- [LPS-114705] Parent method (18e1a8bcf8)
- [LPS-114705] gradle-plugins-test-integration: wait for newly started Tomcat to
actually become reachable, otherwise tests start running while system objects
are still being created (and DataGuardTestRule deletes them) (ce498da5ae)
- [LPS-105380] Change to throw Exception, since that's what the calling method
is doing, so throwing *Exception is not adding value (216100ecdd)
- [LPS-108632] Update gradle (59c48d8cba)

### Dependencies
- [LPS-88645] Update the com.liferay.gradle.util dependency to version 1.0.42.
- [LPS-88645] Update the com.liferay.gradle.util dependency to version 1.0.41.
- [LPS-113624] Update the com.liferay.gradle.util dependency to version 1.0.40.
- [LPS-110422] Update the com.liferay.gradle.util dependency to version 1.0.39.
- [LPS-111896] Update the com.liferay.gradle.util dependency to version 1.0.38.
- [LPS-88645] Update the com.liferay.gradle.util dependency to version 1.0.37.
- [LPS-110283] Update the com.liferay.gradle.util dependency to version 1.0.36.

## 3.0.2 - 2020-03-04

### Commits
- [LPS-106149] Baseline (becb322fa3)
- [LPS-106149] Cacheable tasks (5f1911b5ba)
- [LPS-105380] SF, buy space (03f763c907)
- [LPS-105380] Rename exception variables (b3173da81b)

### Dependencies
- [LPS-106149] Update the com.liferay.gradle.util dependency to version 1.0.35.

## 3.0.1 - 2020-01-07

### Commits
- [LPS-102243] Remove from Gradle plugins (b713dd7982)
- [LPS-102243] Partial revert (b5ee7e9c16)
- [LPS-102243] Remove portal-test-integration and all references to it from
build and classpath (c2efa6c7f2)
- [LPS-100515] Update plugins Gradle version (448efac158)

## 2.4.7 - 2019-08-27

### Commits
- [LPS-100491] Add SuppressWarnings (652c8a75c1)
- [LPS-100491] Remove JMX Java options (1cdf041543)
- [LPS-84119] Do not declare var (85dc5fdf91)

## 2.4.6 - 2019-06-21

### Commits
- [LPS-96247] Update (7385039e86)
- [LPS-96247] Source formatting (aae01ea1d4)
- [LPS-96247] Migrate away from deprecated SourceSetOutput.getClassesDir
(5b0f9860d5)

### Dependencies
- [LPS-96247] Update the com.liferay.gradle.util dependency to version 1.0.34.

## 2.4.5 - 2019-06-15

### Commits
- [LPS-95455] Update testModules configuration (add org.apache.aries.jmx.core)
(47712a89e5)

## 2.4.4 - 2019-06-12

### Commits
- [LPS-95455] Update testModules configuration (use
com.liferay.arquillian.extension.junit.bridge.connector) (879677deda)
- [LPS-85997] Upgrade Tomcat to 9.0.17 (c59adf8378)

## 2.4.3 - 2019-05-08

### Commits
- [LPS-95261] Set JVM arguments for Arquillian (fc9bafcc6c)
- [LPS-85609] Simplify gradleTest (a8b0feff31)
- [LPS-85609] Use Gradle 4.10.2 (9aa90f8961)

## 2.4.2 - 2018-11-19

### Dependencies
- [LPS-87466] Update the com.liferay.gradle.util dependency to version 1.0.33.

## 2.4.1 - 2018-11-16

### Commits
- [LPS-87192] Set the Eclipse task property gradleVersion (040b2abdee)
- [LPS-87192] Add variable gradleVersion (no logic changes) (2f7c0b2fe4)
- [LPS-85609] Fix for CI (test only 4.10.2) (4eed005731)
- [LPS-85609] Test plugins up to Gradle 4.10.2 (60905bc960)
- [LPS-85609] Fix logic (ad3070aa14)
- [LPS-86589] Fix gradle tests (9acc287650)
- [LPS-86589] Test Gradle plugins from Gradle 2.14.1 to 3.5.1 (6df521a506)

### Dependencies
- [LPS-87466] Update the com.liferay.gradle.util dependency to version 1.0.32.

## 2.4.0 - 2018-10-17

### Description
- [LPS-86447] Add the ability to set environment variables for launching
Tomcat.

## 2.3.2 - 2018-10-17

### Commits
- [LPS-86447] semver (529110d199)
- [LPS-86447] add environment input to BaseAppServerTask (69db40ea25)
- [LPS-84119] Move variable declaration inside if/else statement for better
performance (8dd499456b)
- [LPS-71117] Test plugins with Gradle up to 3.5.1 (c3e12d1cf3)

### Dependencies
- [LPS-84094] Update the com.liferay.gradle.util dependency to version 1.0.31.
- [LPS-84094] Update the com.liferay.gradle.util dependency to version 1.0.30.

## 2.3.1 - 2018-08-15

### Commits
- [LPS-83790] Update Tomcat from 9.0.6 to 9.0.10 due to CVE-2018-1336
(5082b1c803)

### Description
- [LPS-83790] Update the `setUpTestableTomcat.zipUrl` property to
`http://archive.apache.org/dist/tomcat/tomcat-9/v9.0.10/bin/apache-tomcat-9.0.10.zip`.

## 2.3.0 - 2018-07-17

### Description
- [LPS-83520] Add the ability to set the application server host name by
setting the property `testIntegrationTomcat.hostName`. The default value is
`localhost`.

## 2.2.2 - 2018-07-17

### Commits
- [LPS-83520] Add ability to set the application server hostName (4db28fab93)
- [LPS-74171] Update Tomcat version from 8.0.32 to 9.0.6 (236900f929)

## 2.2.1 - 2018-03-21

### Commits
- [LPS-78750] add missing directory (9f525bc6e2)
- [LPS-78750] tomcat version doesn't matter just setup skeleton to execute task
(b2af5925d3)
- [LPS-78750] download and copy bundle for test (ed7047eaf9)
- [LPS-78750] Fix Gradle test (6d8223f990)
- [LPS-78750] Simplify (6d1b3f8272)
- [LPS-78750] Exit fast (206a0cbf17)
- [LPS-78750] add test (62269ea547)
- [LPS-78750] move to setUpTestableTomcat action (e93f6423cc)
- [LPS-78750] Make tomcat scripts executable (fd7e617210)
- [LPS-77425] Partial revert of d25f48516a9ad080bcbd50e228979853d3f2dda5
(60d3a950d6)
- [LPS-77425] Partial revert of b739c8fcdc5d1546bd642ca931476c71bbaef1fb
(02ca75b1da)
- [LPS-77425] Increment all major versions (d25f48516a)

### Dependencies
- [LPS-77425] Update the com.liferay.gradle.util dependency to version 1.0.29.
- [LPS-77425] Update the zt-exec dependency to version 1.9.

### Description
- [LPS-78750] Automatically set Tomcat's `.sh` files when executing the
`setUpTestableTomcat` task.

## 2.2.0 - 2017-10-17

### Description
- [LPS-75239] Automatically deploy version 3.0.0 of [Liferay Portal Test] and
[Liferay Portal Test Integration] when executing the `setUpTestableTomcat` task.
- [LPS-75239] Increase the default application server check timeout from 5 to
10 minutes.
- [LPS-75239] Disable the `copyTestModules` task's up-to-date check.
- [LPS-75239] Disable the `copyTestModules` task's behavior of not checking if
a file already exists in the `osgi/test` directory before copying it from the
`testModules` configuration by setting the property
`testIntegrationTomcat.overwriteCopyTestModules` to `false`.

## 2.1.3 - 2017-10-17

### Commits
- [LPS-75239] Disable up-to-date check of "copyTestModules" task (ca65aac88e)
- [LPS-75239] Overwrite old test modules everywhere but in Liferay modules
(170fbbe0d7)
- [LPS-75239] Allow "copyTestModules" task to overwrite old test modules
(c6e53c32e8)
- [LPS-75239] Add portal-test/integration as default "testModules" (e464f29b6f)
- [LPS-75239] Increase check timeout (995d4360f7)

## 2.1.2 - 2017-09-23

### Commits
- [LPS-71117] Bypass https://github.com/gradle/gradle/issues/2343 (b2715307af)

### Description
- [LPS-71117] Bypass the following Gradle 4.0
[issue](https://github.com/gradle/gradle/issues/2343).

## 2.1.1 - 2017-09-20

### Commits
- [LPS-71117] Test with newer Gradle versions (2250353541)
- [LPS-71117] Add Gradle test (15577109d1)
- [LPS-71117] Fix gradle-plugins-test-integration for Gradle 4.0+ (c5b91392ff)

### Description
- [LPS-71117] Add support for Gradle 4.0 and newer.

## 2.1.0 - 2017-07-24

### Commits
- [LPS-73353] Prevent sorting (86c849a213)
- [LPS-73353] Since it's a file, rename property to "File" (ba15397c9d)
- [LPS-73353] Allow to specify closures and callables as values (4cd791b62f)

### Description
- [LPS-73353] Add the ability to configure the JaCoCo Java Agent in the
`setenv.sh` file during the execution of a `SetUpTestableTomcatTask` instance.

## 2.0.1 - 2017-07-24

### Commits
- [LPS-73353] Add jacoco support for tomcat setup (gradle), jacoco agent must be
the first java agent (fc2689517f)

### Dependencies
- [LPS-73584] Update the com.liferay.gradle.util dependency to version 1.0.29.
- [LPS-73584] Update the com.liferay.gradle.util dependency to version 1.0.28.

## 2.0.0 - 2017-07-12

### Description
- [LPS-73525] Add the ability to configure an AspectJ weaver in the `setenv.sh`
file during the execution of a `SetUpTestableTomcatTask` instance.
- [LPS-73525] Automatically set the `JPDA_ADDRESS` environment variable to
`8000` in the `setenv.sh` file during the execution of a
`SetUpTestableTomcatTask` instance.
- [LPS-73525] Remove the `SetUpTestableTomcatTask`'s `catalinaOptsReplacements`
property.

## 1.3.0 - 2017-07-12

### Commits
- [LPS-73525] Allow closures as values for AspectJ properties (5638b10f07)
- [LPS-73525] This is useless now, better remove it (65ea06a900)
- [LPS-73525] SF, group changes to setenv to a new method like what we did in
ant script (fe22939acb)

## 1.2.1 - 2017-07-12

### Commits
- [LPS-73525] Add AspectJ settings to gradle SetUpTestableTomcatTask, make it
consistent with setting logic in ant task "setup-testable-tomcat" (1eab66c85c)
- [LPS-73525] Add jpda settings to gradle SetUpTestableTomcatTask, make it
consistent with setting logic in ant task "setup-testable-tomcat" (a069b6e7af)
- [LPS-73525] Remove/Deprecated unused code (a4ac45fa29)

### Dependencies
- [LPS-72914] Update the com.liferay.gradle.util dependency to version 1.0.27.

## 1.2.0 - 2017-05-11

### Description
- [LPS-72365] Add the ability to deploy additional OSGi modules during the
execution of a `SetUpTestableTomcatTask` instance.
- [LPS-72365] Automatically deploy version 1.1.7 of Apache Aries JMX Core (and
its transitive dependencies) when executing the `setUpTestableTomcat` task.

## 1.1.1 - 2017-05-11

### Commits
- [LPS-72365] Fix "stopTestableTomcat.deleteTestModules" (7dffa0af54)
- [LPS-72365] Deploy Aries JMX if not present (5289598b1a)
- [LPS-72365] Allow to pass additional modules to deploy (c4b23f0e50)
- [LPS-67573] Enable semantic versioning check on CI (63d7f4993f)
- [LPS-70060] Test plugins with Gradle 3.3 (09bed59a42)

## 1.1.0 - 2016-12-01

### Description
- [LPS-67573] Make most methods private in order to reduce API surface.
- [LPS-69492] Add the source directories of the `testIntegration` source set to
the IML file generated by the `idea` task.

## 1.0.10 - 2016-12-01

### Commits
- [LPS-69492] Test if Eclipse and Idea contain the "testIntegration" dirs
(d2eb1b103f)
- [LPS-69492] Add "testIntegration" dirs to Idea (5502af827a)
- [LPS-67573] Export packages (c600f71ec3)
- [LPS-67573] Use logging placeholder (db2f8c8c8a)
- [LPS-67573] Rename wrong variable (e75f56a748)
- [LPS-67573] Use task's logger (7e7c8c4f9e)
- [LPS-67573] Make methods private to reduce API surface (e167a2273c)
- [LPS-67573] Move internal classes to their own packages (cd5e0a4fa4)
- [LPS-67352] SF, enforce empty line after finishing referencing variable
(4ff2bb6038)
- [LPS-67352] Keep empty line between declaring a variable and using it
(ea421a5ab5)
- [LPS-67658] Need "compileOnly" to keep dependencies out of "compile"
(4a3cd0bc9d)
- [LPS-67658] These plugins must work with Gradle 2.5+ (5b963e363d)

## 1.0.9 - 2016-07-08

### Commits
- [LPS-67048] Wait some more after Tomcat is unreachable (b68a554683)
- [LPS-67048] Wait until localhost:8080 is no longer reachable (fe11392c13)
- [LPS-67048] Move check properties to base class (ae865b9783)

## 1.0.8 - 2016-06-29

### Commits
- [LPS-66873] Add warning if unable to replace string in setenv.* (0a71c0f25a)
- [LPS-66873] Rename everything else to "SetUp" (23cf7f7c5c)
- [LPS-66873] Rename tasks to "SetUp*Task" (441641719b)
- [LPS-66873] Temporarily rename tasks to "SetUp*Task1" (6eac9c18bd)
- [LPS-66873] Add option to replace strings in setenv.* (9ab027bd43)
- [LPS-66873] These methods only throw IOException (2fe3d7c869)

## 1.0.7 - 2016-06-27

### Commits
- [LPS-65749] "testSrcDirs" property is deprecated and not used by Gradle
(4437123328)

## 1.0.6 - 2016-06-16

### Commits
- [LPS-65749] Closures with null owners don't work in Gradle 2.14 (b42316699d)

### Dependencies
- [LPS-65749] Update the com.liferay.gradle.util dependency to version 1.0.26.

## 1.0.5 - 2016-05-26

### Commits
- [LPS-66139] Check console to find out if Tomcat has started (29d00ef89d)
- [LPS-66139] stderr is already redirected to stdout (9e1dea1521)
- [LPS-66139] Rename for more clarity (8d9b4389a1)
- [LPS-65810] Gradle plugins aren't used in OSGi, no need to export anything
(83cdd8ddcd)

### Dependencies
- [LPS-66139] Update the zt-exec dependency to version 1.9.

## 1.0.4 - 2016-05-09

### Commits
- [LPS-65067] Use and configure the new task (82371b2355)
- [LPS-65067] Implement the new interface (05e10d5af5)
- [LPS-65067] Add task to remove test modules in osgi/modules at the end
(13804f97d8)
- [LPS-65067] Don't overwrite test modules in osgi/modules if already exist
(5c0891a39f)
- [LPS-61099] Delete build.xml in modules (c9a7e1d370)

### Dependencies
- [LPS-65086] Update the com.liferay.gradle.util dependency to version 1.0.25.

## 1.0.3 - 2016-03-23

### Commits
- [LPS-64586] liferayHome is required only if deleteLiferayHome == true
(40ff8594a2)
- [LPS-64586] Prevent NPE if the file is a callable which returns null
(c8cf48659c)
- [LPS-64586] No reason to use closures here (ea19b57ef9)
- [LPS-64586] Add task default values for easier plugin reuse (dfb12bed19)
- [LPS-64586] Cleanup static list after stopping the app server (ee4f97c2be)
- [LPS-61420] Move leading space to previous append value (77b3b89217)
- [LPS-63943] This is done automatically now (f1e42382d9)
- [LPS-61420] Source formatting (0df5dd83d6)

## 1.0.2 - 2016-02-11

### Commits
- [LPS-63166] Look for test Java files instead of classes (5d18ed8dca)
- [LPS-63166] Extract method (0f3746d132)
- [LPS-63166] Search inside any src directory, not just the first one
(683ed79c13)
- [LPS-63166] Categorize tasks into groups (c4956e12e1)
- [LPS-63166] Add task descriptions (72c8ba11ba)
- [LPS-62883] Update gradle-plugins/build.gradle (20fc2457e6)
- [LPS-62942] Explicitly list exported packages for correctness (f095a51e25)

## 1.0.1 - 2016-01-27

### Commits
- [LPS-62589] Write one EOL before starting new commands (5975c0c222)
- [LPS-61088] Remove classes and resources dir from Include-Resource
(1b0e1275bc)
- [LPS-62111] Sort (2e70f94148)
- [LPS-62111] Add @Override (7d6efeca5a)

[CVE-2021]: https://issues.liferay.com/browse/CVE-2021
[LPD-15162]: https://issues.liferay.com/browse/LPD-15162
[LPD-21549]: https://issues.liferay.com/browse/LPD-21549
[LPD-31864]: https://issues.liferay.com/browse/LPD-31864
[LPD-37353]: https://issues.liferay.com/browse/LPD-37353
[LPS-51081]: https://issues.liferay.com/browse/LPS-51081
[LPS-61088]: https://issues.liferay.com/browse/LPS-61088
[LPS-61099]: https://issues.liferay.com/browse/LPS-61099
[LPS-61420]: https://issues.liferay.com/browse/LPS-61420
[LPS-62111]: https://issues.liferay.com/browse/LPS-62111
[LPS-62589]: https://issues.liferay.com/browse/LPS-62589
[LPS-62883]: https://issues.liferay.com/browse/LPS-62883
[LPS-62942]: https://issues.liferay.com/browse/LPS-62942
[LPS-63166]: https://issues.liferay.com/browse/LPS-63166
[LPS-63943]: https://issues.liferay.com/browse/LPS-63943
[LPS-64586]: https://issues.liferay.com/browse/LPS-64586
[LPS-65067]: https://issues.liferay.com/browse/LPS-65067
[LPS-65086]: https://issues.liferay.com/browse/LPS-65086
[LPS-65749]: https://issues.liferay.com/browse/LPS-65749
[LPS-65810]: https://issues.liferay.com/browse/LPS-65810
[LPS-66139]: https://issues.liferay.com/browse/LPS-66139
[LPS-66873]: https://issues.liferay.com/browse/LPS-66873
[LPS-67048]: https://issues.liferay.com/browse/LPS-67048
[LPS-67352]: https://issues.liferay.com/browse/LPS-67352
[LPS-67573]: https://issues.liferay.com/browse/LPS-67573
[LPS-67658]: https://issues.liferay.com/browse/LPS-67658
[LPS-69492]: https://issues.liferay.com/browse/LPS-69492
[LPS-70060]: https://issues.liferay.com/browse/LPS-70060
[LPS-71117]: https://issues.liferay.com/browse/LPS-71117
[LPS-72365]: https://issues.liferay.com/browse/LPS-72365
[LPS-72914]: https://issues.liferay.com/browse/LPS-72914
[LPS-73353]: https://issues.liferay.com/browse/LPS-73353
[LPS-73525]: https://issues.liferay.com/browse/LPS-73525
[LPS-73584]: https://issues.liferay.com/browse/LPS-73584
[LPS-74171]: https://issues.liferay.com/browse/LPS-74171
[LPS-75239]: https://issues.liferay.com/browse/LPS-75239
[LPS-77425]: https://issues.liferay.com/browse/LPS-77425
[LPS-78750]: https://issues.liferay.com/browse/LPS-78750
[LPS-83520]: https://issues.liferay.com/browse/LPS-83520
[LPS-83790]: https://issues.liferay.com/browse/LPS-83790
[LPS-84094]: https://issues.liferay.com/browse/LPS-84094
[LPS-84119]: https://issues.liferay.com/browse/LPS-84119
[LPS-85609]: https://issues.liferay.com/browse/LPS-85609
[LPS-85997]: https://issues.liferay.com/browse/LPS-85997
[LPS-86447]: https://issues.liferay.com/browse/LPS-86447
[LPS-86589]: https://issues.liferay.com/browse/LPS-86589
[LPS-87192]: https://issues.liferay.com/browse/LPS-87192
[LPS-87466]: https://issues.liferay.com/browse/LPS-87466
[LPS-88645]: https://issues.liferay.com/browse/LPS-88645
[LPS-95261]: https://issues.liferay.com/browse/LPS-95261
[LPS-95455]: https://issues.liferay.com/browse/LPS-95455
[LPS-96247]: https://issues.liferay.com/browse/LPS-96247
[LPS-100491]: https://issues.liferay.com/browse/LPS-100491
[LPS-100515]: https://issues.liferay.com/browse/LPS-100515
[LPS-102243]: https://issues.liferay.com/browse/LPS-102243
[LPS-105380]: https://issues.liferay.com/browse/LPS-105380
[LPS-106149]: https://issues.liferay.com/browse/LPS-106149
[LPS-108632]: https://issues.liferay.com/browse/LPS-108632
[LPS-110283]: https://issues.liferay.com/browse/LPS-110283
[LPS-110422]: https://issues.liferay.com/browse/LPS-110422
[LPS-111291]: https://issues.liferay.com/browse/LPS-111291
[LPS-111896]: https://issues.liferay.com/browse/LPS-111896
[LPS-113624]: https://issues.liferay.com/browse/LPS-113624
[LPS-114024]: https://issues.liferay.com/browse/LPS-114024
[LPS-114705]: https://issues.liferay.com/browse/LPS-114705
[LPS-115020]: https://issues.liferay.com/browse/LPS-115020
[LPS-125871]: https://issues.liferay.com/browse/LPS-125871
[LPS-129193]: https://issues.liferay.com/browse/LPS-129193
[LPS-129995]: https://issues.liferay.com/browse/LPS-129995
[LPS-130505]: https://issues.liferay.com/browse/LPS-130505
[LPS-135481]: https://issues.liferay.com/browse/LPS-135481
[LPS-138183]: https://issues.liferay.com/browse/LPS-138183
[LPS-143280]: https://issues.liferay.com/browse/LPS-143280
[LPS-143837]: https://issues.liferay.com/browse/LPS-143837
[LPS-150378]: https://issues.liferay.com/browse/LPS-150378
[LPS-150857]: https://issues.liferay.com/browse/LPS-150857
[LPS-164410]: https://issues.liferay.com/browse/LPS-164410
[LPS-169038]: https://issues.liferay.com/browse/LPS-169038
[LPS-172797]: https://issues.liferay.com/browse/LPS-172797
[LPS-179403]: https://issues.liferay.com/browse/LPS-179403
[LPS-181508]: https://issues.liferay.com/browse/LPS-181508
[LPS-185311]: https://issues.liferay.com/browse/LPS-185311
[LPS-187234]: https://issues.liferay.com/browse/LPS-187234
[LPS-193415]: https://issues.liferay.com/browse/LPS-193415
[LPS-199260]: https://issues.liferay.com/browse/LPS-199260
[LPS-202894]: https://issues.liferay.com/browse/LPS-202894
[LRCI-2670]: https://issues.liferay.com/browse/LRCI-2670
[LRQA-82692]: https://issues.liferay.com/browse/LRQA-82692