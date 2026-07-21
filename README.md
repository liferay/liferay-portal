# Liferay DXP

> 🚀 **See what's next and meet the team behind it at** &nbsp; [![Devcon London, November 2026 — event details and registration](https://img.shields.io/badge/Devcon%20London%20%C2%B7%20November%202026-E500BD)](https://events.liferay.com/5NAl2R?utm_source=Github&utm_campaign=Github-Devcon26&utm_medium=organic&RefId=Github)

This `liferay-portal` repository contains the source code for [Liferay DXP](https://www.liferay.com/products/dxp), Liferay's Java-based digital experience platform. Liferay Portal and Liferay DXP are now a single product. Building this repository produces a Liferay DXP bundle. The platform is maintained by Liferay engineering and a global community of contributors.

## Getting Liferay DXP

Run Liferay DXP in any of these ways:

* **Build from source** (this repository): Get the latest unreleased fixes, customize the platform, or contribute changes upstream. Build instructions are below.

* **[Liferay DXP Free Tier](https://www.liferay.com/downloads-community)**: The fastest way to run Liferay DXP. Register for a Free Tier license and download a ready-to-run bundle. Recommended for evaluation and most nonproduction use. Includes clustering up to three nodes.

* **Liferay DXP Enterprise Subscription**: Adds enterprise features (multifactor authentication, SAML, advanced analytics, production-scale clustering), certified releases, and Liferay Support. [Contact sales](https://www.liferay.com/contact-sales).

If you previously ran Liferay Portal CE, the recommended upgrade path is [Liferay DXP Free Tier](https://www.liferay.com/downloads-community). You can also build Liferay DXP from this repository.

## Building Liferay DXP from Source

### Prerequisites

Install these tools before building:

* JDK 21
* [Apache Ant](https://ant.apache.org) 1.10.14 or higher
* [Git](https://git-scm.com)
* [Liferay Blade CLI](https://learn.liferay.com/w/dxp/development/tooling/blade-cli) (only needed if you plan to build client extensions or OSGi modules)

The build uses the included Gradle wrapper, so you do not need to install Gradle separately.

### Build Steps

1. Create a working directory and `cd` into it. You clone two repositories side by side here:

	```bash
	mkdir liferay-dev
	cd liferay-dev
	```

1. (Recommended) Clone [`liferay-binaries-cache-2020`](https://github.com/liferay/liferay-binaries-cache-2020) to speed up builds. Without it, the build downloads dependencies on demand.

	```bash
	git clone https://github.com/liferay/liferay-binaries-cache-2020 --branch master --single-branch --depth 1
	```

1. Fork [`liferay/liferay-portal`](https://github.com/liferay/liferay-portal) on GitHub, then clone your fork next to `liferay-binaries-cache-2020`:

	```bash
	git clone https://github.com/<your-github-user>/liferay-portal
	cd liferay-portal
	```

1. Add the upstream repository so you can fetch changes from `liferay/liferay-portal`:

	```bash
	git remote add upstream https://github.com/liferay/liferay-portal
	```

1. Build the bundle:

	```bash
	ant all
	```

	The bundle is unpacked into `../bundles` and contains a Tomcat application server with Liferay DXP deployed.

1. Start the bundle:

	```bash
	../bundles/tomcat-<version>/bin/startup.sh
	```

	Then visit `http://localhost:8080`.

For more details, including IDE setup, deploying core changes, deploying modules, and submitting pull requests, see [Building Liferay DXP from Source](https://learn.liferay.com/w/reference/contributing-to-liferay-development/building-liferay-source) on Liferay Learn.

## Modules

Liferay DXP includes hundreds of OSGi modules under [`modules`](modules). The build system, marker files, and Gradle conventions used by these modules are documented in [`modules/README.md`](modules/README.md).

## Contributing

Contributions are welcome. See [`CONTRIBUTING.md`](CONTRIBUTING.md) to get started.

## License

`SPDX-License-Identifier: (LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06)`

See [`LICENSING.md`](LICENSING.md) for details.