#!/bin/bash
#
# Bootstrap all build dependencies that Liferay's dead public repos
# (cdn.lfrs.sl, repository.liferay.com) can no longer serve, so that a fresh
# git clone can run `ant clean all` successfully.
#
# Idempotent — safe to re-run; everything is check-before-fetch.
#
# Sources used (in order of preference):
#   1. Maven Central            — mirrors all released com.liferay artifacts
#   2. liferay-binaries-cache-2017 (github.com/liferay) — Liferay's official
#      offline dependency cache; covers repackaged/snapshot-only artifacts
#   3. Axiell Artifactory       — Axiell-built artifacts (lexicon webjar)
#   4. Checked-in lib/**.jar    — for lib/*/dependencies.properties oddballs
#
# Requirements: bash, curl, unzip; `gh` CLI (authenticated) recommended for
# GitHub API calls — falls back to unauthenticated curl (60 req/h limit,
# needs python3 for JSON parsing).

set -u

PORTAL=$(cd "$(dirname "$0")/.." && pwd)
M2=$PORTAL/.m2
CENTRAL=https://repo1.maven.org/maven2
CACHE_REPO=liferay/liferay-binaries-cache-2017
CACHE_PATH=.gradle/caches/modules-2/files-2.1
ARTIFACTORY=https://artifactory.axiell.com/artifactory/simple/ext-release-local
UNRESOLVED=0

api() { # <path> <jq-filter: .[].path | .[] name+download_url | .download_url>
	if command -v gh > /dev/null 2>&1; then
		gh api "repos/$CACHE_REPO/contents/$1" --jq "$2" 2>/dev/null
	else
		curl -sfL --max-time 30 "https://api.github.com/repos/$CACHE_REPO/contents/$1" |
			python3 -c "
import json, sys
d = json.load(sys.stdin)
f = '''$2'''
if isinstance(d, dict):
    print(d.get('download_url') or '')
elif f == '.[].path':
    [print(e['path']) for e in d]
else:
    [print(e['name'] + ' ' + (e['download_url'] or '')) for e in d]
" 2>/dev/null
	fi
}

central_has() { # group name version [classifier]
	local gpath=${1//./\/} jar="$2-$3.jar"
	[ -n "${4:-}" ] && jar="$2-$3-$4.jar"
	[ "$(curl -s -o /dev/null -w "%{http_code}" --max-time 20 -I -L \
		"$CENTRAL/$gpath/$2/$3/$jar")" = "200" ]
}

write_pom() { # group name version destdir
	printf '<?xml version="1.0" encoding="UTF-8"?>\n<project xmlns="http://maven.apache.org/POM/4.0.0">\n\t<modelVersion>4.0.0</modelVersion>\n\t<groupId>%s</groupId>\n\t<artifactId>%s</artifactId>\n\t<version>%s</version>\n\t<packaging>jar</packaging>\n</project>\n' \
		"$1" "$2" "$3" > "$4/$2-$3.pom"
}

in_m2() { # group name version [classifier]
	local jar="$2-$3.jar"
	[ -n "${4:-}" ] && jar="$2-$3-$4.jar"
	[ -f "$M2/${1//./\//}/$2/$3/$jar" ]
}

fetch_binaries_cache() { # group name version -> installs jar+pom into .m2
	local group=$1 name=$2 version=$3
	local listing dest got="" shadir files fname furl
	listing=$(api "$CACHE_PATH/$group/$name/$version" '.[].path')
	[ -z "$listing" ] && return 1
	dest=$M2/${group//./\//}/$name/$version
	mkdir -p "$dest"
	while read -r shadir; do
		[ -z "$shadir" ] && continue
		files=$(api "$shadir" '.[] | .name + " " + .download_url')
		while read -r fname furl; do
			case $fname in
				*.jar | *.pom) curl -sfL --max-time 300 -o "$dest/$fname" "$furl" && got="$got $fname" ;;
			esac
		done <<< "$files"
	done <<< "$listing"
	[ -n "$got" ] || { rmdir "$dest" 2>/dev/null; return 1; }
	# a pom with a <parent> drags in an unresolvable parent chain (e.g. ical4j
	# -> net.modularity:modularity-parent); replace with a minimal pom
	if [ ! -f "$dest/$name-$version.pom" ] || grep -q "<parent>" "$dest/$name-$version.pom"; then
		write_pom "$group" "$name" "$version" "$dest"
	fi
	echo "  fetched $group:$name:$version (binaries-cache)"
}

install_url() { # group name version url
	local dest=$M2/${1//./\//}/$2/$3
	mkdir -p "$dest"
	curl -sfL --max-time 300 -o "$dest/$2-$3.jar" "$4" || { rmdir "$dest" 2>/dev/null; return 1; }
	write_pom "$1" "$2" "$3" "$dest"
	echo "  installed $1:$2:$3"
}

ensure() { # group name version — .m2 -> Central -> binaries-cache
	in_m2 "$1" "$2" "$3" && return 0
	central_has "$1" "$2" "$3" && return 0
	fetch_binaries_cache "$1" "$2" "$3" && return 0
	echo "  UNRESOLVED $1:$2:$3"
	UNRESOLVED=$((UNRESOLVED + 1))
	return 1
}

echo "== 1/6 Gradle distribution zip (tools/, not tracked in git)"
GRADLE_ZIP=gradle-3.3.LIFERAY-PATCHED-1-bin.zip
if [ ! -f "$PORTAL/tools/$GRADLE_ZIP" ]; then
	url=$(api "$GRADLE_ZIP" '.download_url' | head -1)
	[ -z "$url" ] && url="https://raw.githubusercontent.com/$CACHE_REPO/master/$GRADLE_ZIP"
	curl -sfL --max-time 900 -o "$PORTAL/tools/$GRADLE_ZIP" "$url" &&
		echo "  fetched tools/$GRADLE_ZIP" ||
		{ echo "  UNRESOLVED tools/$GRADLE_ZIP"; UNRESOLVED=$((UNRESOLVED + 1)); }
else
	echo "  present"
fi

echo "== 2/6 SDK bootstrap Ivy jar (~/.liferay/mirrors, exact dead-snapshot path)"
IVY_SNAP=2.4.0.LIFERAY-PATCHED-1-SNAPSHOT
IVY_REL=2.4.0.LIFERAY-PATCHED-1
IVY_DIR=$HOME/.liferay/mirrors/cdn.lfrs.sl/repository.liferay.com/nexus/content/repositories/liferay-public-snapshots/com/liferay/org.apache.ivy/$IVY_SNAP
if [ ! -f "$IVY_DIR/org.apache.ivy-$IVY_SNAP.jar" ]; then
	mkdir -p "$IVY_DIR"
	curl -sfL --max-time 300 -o "$IVY_DIR/org.apache.ivy-$IVY_SNAP.jar" \
		"$CENTRAL/com/liferay/org.apache.ivy/$IVY_REL/org.apache.ivy-$IVY_REL.jar" &&
		md5sum "$IVY_DIR/org.apache.ivy-$IVY_SNAP.jar" | awk '{print $1}' \
			> "$IVY_DIR/org.apache.ivy-$IVY_SNAP.jar.md5" &&
		echo "  seeded (release $IVY_REL from Central)" ||
		{ echo "  UNRESOLVED ivy bootstrap jar"; UNRESOLVED=$((UNRESOLVED + 1)); }
else
	echo "  present"
fi

echo "== 3/6 lib/*/dependencies.properties artifacts missing from Central"
for dir in development global portal; do
	props=$PORTAL/lib/$dir/dependencies.properties
	[ -f "$props" ] || continue
	while IFS='=' read -r title coord; do
		[ -z "$title" ] && continue
		case $title in \#*) continue ;; esac
		IFS=':' read -r group name version classifier <<< "$coord"
		in_m2 "$group" "$name" "$version" "${classifier:-}" && continue
		central_has "$group" "$name" "$version" "${classifier:-}" && continue
		src=$PORTAL/lib/$dir/$title.jar
		if [ -f "$src" ]; then
			dest=$M2/${group//./\//}/$name/$version
			mkdir -p "$dest"
			jar="$name-$version.jar"
			[ -n "${classifier:-}" ] && jar="$name-$version-$classifier.jar"
			cp "$src" "$dest/$jar"
			write_pom "$group" "$name" "$version" "$dest"
			echo "  installed $coord (from lib/$dir/$title.jar)"
		else
			fetch_binaries_cache "$group" "$name" "$version" ||
				{ echo "  UNRESOLVED $coord"; UNRESOLVED=$((UNRESOLVED + 1)); }
		fi
	done < "$props"
done

echo "== 4/6 Pinned *-LIFERAY-CACHED Equinox artifacts (binaries-cache)"
ensure com.liferay org.eclipse.osgi 3.10.200-20150904.172142-1-LIFERAY-CACHED
ensure com.liferay org.eclipse.osgi.services 3.5.0-20150611.165350-3-LIFERAY-CACHED
ensure com.liferay org.eclipse.equinox.metatype 1.4.200-20150831.175616-1-LIFERAY-CACHED
ensure com.liferay org.eclipse.equinox.console 1.1.100-20150308.220103-2-LIFERAY-CACHED

echo "== 5/6 Special-source artifacts"
# Axiell-built lexicon webjar — only on Axiell Artifactory (anonymous read)
if ! in_m2 com.liferay.webjars com.liferay.webjars.lexicon 1.0.25a; then
	install_url com.liferay.webjars com.liferay.webjars.lexicon 1.0.25a \
		"$ARTIFACTORY/com/liferay/webjars/com.liferay.webjars.lexicon/1.0.25a/com.liferay.webjars.lexicon-1.0.25a.jar" ||
		{ echo "  UNRESOLVED com.liferay.webjars.lexicon:1.0.25a"; UNRESOLVED=$((UNRESOLVED + 1)); }
fi
# ojdbc8 is published on Central under com.oracle.database.jdbc.
if ! in_m2 com.oracle.database.jdbc ojdbc8 23.26.2.0.0; then
	install_url com.oracle.database.jdbc ojdbc8 23.26.2.0.0 \
		"$CENTRAL/com/oracle/database/jdbc/ojdbc8/23.26.2.0.0/ojdbc8-23.26.2.0.0.jar" ||
		{ echo "  UNRESOLVED com.oracle.database.jdbc:ojdbc8:23.26.2.0.0"; UNRESOLVED=$((UNRESOLVED + 1)); }
fi
# jamwiki — declared with a version variable, missed by the literal sweep below
ensure com.liferay org.jamwiki 1.0.7

echo "== 6/7 Sweep literal dep coordinates in modules/**/build.gradle"
coords=$(
	{
		grep -rhoE 'group: "[^"]+", name: "[^"]+",( transitive: (true|false),)? version: "[^"]+"' \
			"$PORTAL/modules" --include=build.gradle |
			sed -E 's/group: "([^"]+)", name: "([^"]+)",( transitive: (true|false),)? version: "([^"]+)"/\1:\2:\5/'
		grep -rhoE '"[A-Za-z0-9_.-]+:[A-Za-z0-9_.-]+:[0-9][A-Za-z0-9_.-]*"' \
			"$PORTAL/modules" --include=build.gradle | tr -d '"'
	} | sort -u
)
while IFS=':' read -r group name version; do
	[ -z "$group" ] && continue
	# skip placeholders, locally-built portal artifacts, test-only snapshots
	case $version in
		default | latest.release | *\$* | *-SNAPSHOT) continue ;;
	esac
	case $group in
		com.liferay.portal | com.liferay.arquillian) continue ;;
	esac
	ensure "$group" "$name" "$version"
done <<< "$coords"

echo "== 7/7 SDK ivy.xml dependencies (:ivySetUpSdk / :ivySetUp* configs)"
SDK_VERSION=$(sed -n 's/^[[:space:]]*build.sdk.version=//p' "$PORTAL/build.properties" | head -1)
SDK_IVY_XMLS=""
if [ -f "$PORTAL/tools/sdk/ivy.xml" ]; then
	SDK_IVY_XMLS=$(cat "$PORTAL/tools/sdk/ivy.xml" "$PORTAL"/tools/sdk/dependencies/*/ivy.xml 2>/dev/null)
else
	sdk_zip=$(mktemp)
	if curl -sfL --max-time 900 -o "$sdk_zip" \
		"$CENTRAL/com/liferay/portal/com.liferay.portal.plugins.sdk/$SDK_VERSION/com.liferay.portal.plugins.sdk-$SDK_VERSION.zip"; then
		# only the files util.gradle's setUp* tasks read: <sdk-root>/ivy.xml and
		# <sdk-root>/dependencies/*/ivy.xml (a bare "*/ivy.xml" unzip glob would
		# also match tools/templates/**, whose JSF deps the build never resolves)
		ivy_entries=$(unzip -Z1 "$sdk_zip" 2>/dev/null |
			grep -E "^[^/]+/(ivy\.xml|dependencies/[^/]+/ivy\.xml)$")
		SDK_IVY_XMLS=$(while read -r e; do unzip -p "$sdk_zip" "$e" 2>/dev/null; done <<< "$ivy_entries")
	else
		echo "  UNRESOLVED SDK zip $SDK_VERSION (cannot enumerate ivy.xml deps)"
		UNRESOLVED=$((UNRESOLVED + 1))
	fi
	rm -f "$sdk_zip"
fi
sdk_deps=$(grep -oE '<dependency [^>]*/>' <<< "$SDK_IVY_XMLS" | sort -u)
while read -r dep; do
	[ -z "$dep" ] && continue
	group=$(sed -n 's/.*org="\([^"]*\)".*/\1/p' <<< "$dep")
	name=$(sed -n 's/.*name="\([^"]*\)".*/\1/p' <<< "$dep")
	version=$(sed -n 's/.*rev="\([^"]*\)".*/\1/p' <<< "$dep")
	[ -z "$group" ] || [ -z "$name" ] || [ -z "$version" ] && continue
	case $version in *\$* | latest.*) continue ;; esac
	ensure "$group" "$name" "$version"
done <<< "$sdk_deps"

# defensive: node_modules restored without symlinks break npm binstubs
broken=$(find "$PORTAL/modules" -path "*/node_modules/.bin/*" -type f \
	! -path "*/node_modules/*/node_modules/*" 2>/dev/null |
	sed 's|/node_modules/\.bin/.*||' | sort -u)
if [ -n "$broken" ]; then
	echo "== extra: wiping node_modules with non-symlink binstubs"
	while read -r d; do
		rm -rf "$d/node_modules" && echo "  wiped ${d#"$PORTAL"/}/node_modules"
	done <<< "$broken"
fi

echo
if [ "$UNRESOLVED" -gt 0 ]; then
	echo "DONE with $UNRESOLVED unresolved artifact(s) — build may still fail."
	exit 1
fi
echo "DONE — all dependencies staged; 'ant clean all' should now succeed."
