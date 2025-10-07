# Liferay Ant Sync Dir Change Log

## 1.0.3 - 2025-01-07

### Commits
- [LPD-34518] Fix compile, source option 6 is no longer supported by jdk17
(e84a22ccfd)
- [LRCI-4941] Only sync the directory once per Jenkins build (d7dda47562)
- [LPD-36975] liferay-portal: auto SF (b1c315ad65)
- [LPS-105380] SF, inline (e11238b1a3)
- [LPS-115364] Update ant to 1.9.15 (2c4390c048)
- [LPS-105380] Rename exception variables (b3173da81b)

### Dependencies
- [LPD-36975] Update the ant dependency to version 1.10.14.
- [LPS-137126] Update the ant dependency to version 1.10.11.
- [LPS-129842] Update the ant dependency to version 1.10.9.
- [LPS-115364] Update the ant dependency to version 1.9.15.

## 1.0.2 - 2019-07-30

### Commits
- [LRCI-498] Simplify how synchronized files are counted. (6bc7f0571d)
- [LRCI-498] Remove redundant code (22094d3b37)
- [LRCI-498] Make thread pool size configurable (9f75d1c6c0)
- [LRCI-498] Make sure each file is counted properly (f5a9073abd)
- [LRCI-498] Simplify (1e2f8d53f5)
- [LRCI-498] Create symbolic link if symbolic attribute is set to TRUE
(946c5c492d)
- [LPS-98801] [LPS-96095] auto SF for ant (bf3c0ef390)
- [LPS-77425] Partial revert of d25f48516a9ad080bcbd50e228979853d3f2dda5
(60d3a950d6)
- [LPS-77425] Increment all major versions (d25f48516a)

### Dependencies
- [LPS-98801 LPS-96095] Update the ant dependency to version 1.9.14.
- [LPS-75049] Update the ant dependency to version 1.9.4.

## 1.0.1 - 2017-08-01

### Commits
- [LPS-73858] Show IO errors (2874aaed37)
- [LPS-73858] Rename (0b324d762f)
- [LPS-73858] Increment only if successful (f9ed18d0de)
- [LPS-73858] No need to return anything (c042a57ec0)
- [LPS-73858] Add author (5fc60678fa)
- [LPS-73858] This method does not throw any exception (785925c22b)
- [LPS-73858] Update to specify JDK6 (929ca92937)
- [LPS-73858] Switch to using JDK6 API (e92a2fb1e6)

[LPD-34518]: https://issues.liferay.com/browse/LPD-34518
[LPD-36975]: https://issues.liferay.com/browse/LPD-36975
[LPS-73858]: https://issues.liferay.com/browse/LPS-73858
[LPS-75049]: https://issues.liferay.com/browse/LPS-75049
[LPS-77425]: https://issues.liferay.com/browse/LPS-77425
[LPS-96095]: https://issues.liferay.com/browse/LPS-96095
[LPS-98801]: https://issues.liferay.com/browse/LPS-98801
[LPS-105380]: https://issues.liferay.com/browse/LPS-105380
[LPS-115364]: https://issues.liferay.com/browse/LPS-115364
[LPS-129842]: https://issues.liferay.com/browse/LPS-129842
[LPS-137126]: https://issues.liferay.com/browse/LPS-137126
[LRCI-498]: https://issues.liferay.com/browse/LRCI-498
[LRCI-4941]: https://issues.liferay.com/browse/LRCI-4941