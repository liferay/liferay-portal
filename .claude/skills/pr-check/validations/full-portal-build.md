# Full Portal Build

## Trigger

- portal-core changed: `portal-impl/**`, `portal-kernel/**`, `portal-test/**`, `portal-web/**`, `support-tomcat/**`, `util-bridges/**`, `util-java/**`, `util-slf4j/**`, `util-taglib/**`. Mandatory in this case — no Gradle deploy path covers these sources.

	A portal-core change of only `*.properties` files does not fire this validation. `ant all` gives no signal for them, and **Source Format** covers them instead.

- OR [per-module-compile.md](per-module-compile.md) escalated to it, because the deploy set it derived is large enough that one full build is cheaper than deploying each module. Compare:

	- **Full Portal Build cost** = 8 min (the `ant all` baseline).

	- **Per-Module Compile cost** = 3 min setup, then each module a separate `gradlew` invocation paying its own configuration, measured at about 11 sec warm and a minute or more cold, almost all of it per invocation overhead. The cost is linear in N, so the two meet near 44 modules warm and well below that cold, which is why the handoff there fires above 40.

	This branch has no regex, since the deploy set size is not known until Pass 2 derives it. Pass 1 cannot select on it, so **Per-Module Compile** hands off instead. Show the cost math when this branch runs so the developer can override.

## Match

`^(portal-impl|portal-kernel|portal-test|portal-web|support-tomcat|util-bridges|util-java|util-slf4j|util-taglib)/ &! \.properties$`

## Command

```bash
ant all -Dgradle.stop.daemon.enabled=false
```

`ant all` is `clean` + `compile` + `deploy`; the deploy target's marketplace branch deploys every project with a `.lfrbuild-portal` marker.

## Notes

When this fires, **Per-Module Compile** still runs for any modules in the touched set without a `.lfrbuild-portal` marker (`ant all`'s marketplace branch only deploys modules with the marker), and **Integration Test Compile** is obviated for `.lfrbuild-portal` modules. Both assume the build succeeded. A Full Portal Build that failed produced no compile signal, so it obviates nothing, and everything it would have covered still needs its own run.

## Time Estimate

~8 min.