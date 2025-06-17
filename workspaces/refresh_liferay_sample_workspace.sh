#!/bin/bash

cd $(dirname "${0}")

function check_blade {
	if [ -e ~/jpm/bin/blade ]
	then
		BLADE_PATH=~/jpm/bin/blade
	fi

	if [ -e ~/Library/PackageManager/bin/blade ]
	then
		BLADE_PATH=~/Library/PackageManager/bin/blade
	fi

	if [ -z "${BLADE_PATH}" ]
	then
		echo "Blade CLI is not available. To install Blade CLI, execute the following command:"
		echo ""

		echo "curl -L https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/cli/installers/local | sh"

		exit 1
	fi

	#
	# Update Blade with Blade.
	#

	#${BLADE_PATH} update -s > /dev/null

	#
	# Update Blade directly with JPM.
	#

	#jpm install -f https://repository-cdn.liferay.com/nexus/service/local/repositories/liferay-public-releases/content/com/liferay/blade/com.liferay.blade.cli/4.1.1/com.liferay.blade.cli-4.1.1.jar
}

function refresh_liferay_sample_workspace {

	#
	# Sample Workspace
	#

	local temp_dir=$(mktemp -d)

	pushd ${temp_dir}

	${BLADE_PATH} init -v $(${BLADE_PATH} init --all --list | grep dxp-7.4 | head -1)

	echo -en "\n**/dist\n**/node_modules_cache\n.DS_Store" >> .gitignore

	echo -en "\n\nfeature.flag.LPS-172903=true\nfeature.flag.LPS-164563=true\nfeature.flag.LPS-177027=true" >> configs/local/portal-ext.properties

	#echo -en "\nliferay.workspace.docker.image.liferay=liferay/dxp:7.4.13-u54-d5.0.5-20221208173455" >> gradle.properties
	echo -en "\nliferay.workspace.node.package.manager=yarn" >> gradle.properties

	#
	# https://stackoverflow.com/questions/1654021/how-can-i-delete-a-newline-if-it-is-the-last-character-in-a-file
	# https://stackoverflow.com/questions/38256431/bash-sort-ignore-first-5-lines
	#

	{ head -n 5 gradle.properties ; tail -n +6 gradle.properties | sort | perl -e "chomp if eof" -p; } >gradle.properties.tmp

	mv gradle.properties.tmp gradle.properties

	sed -i 's/name: "biz.aQute.bnd", version: ".*"/name: "biz.aQute.bnd.gradle", version: "5.2.0"/' settings.gradle
	sed -i 's/name: "com.liferay.gradle.plugins.workspace", version: ".*"/name: "com.liferay.gradle.plugins.workspace", version: "13.0.9"/' settings.gradle

	echo -en "\ninclude \"poshi\"" >> settings.gradle

	popd

	cat << EOF > liferay-sample-workspace/.gitignore
.DS_Store
/.idea
/bundles
/poshi/poshi-ext.properties
/poshi/test-results
/poshi/tests
bin
build
dist
node_modules
node_modules_cache
EOF

	truncate -s -1 liferay-sample-workspace/.gitignore

	cp ${temp_dir}/gradle.properties liferay-sample-workspace
	cp ${temp_dir}/gradlew liferay-sample-workspace
	cp ${temp_dir}/settings.gradle liferay-sample-workspace

	cp -R ${temp_dir}/gradle liferay-sample-workspace

	mkdir -p liferay-sample-workspace/configs/local

	cp ${temp_dir}/configs/local/portal-ext.properties liferay-sample-workspace/configs/local

	mkdir -p liferay-sample-workspace/modules

	echo "Client extensions are the recommended way of customizing Liferay. Modules and" > liferay-sample-workspace/modules/README.md
	echo -n "themes are supported for backwards compatibility." >> liferay-sample-workspace/modules/README.md

	mkdir -p liferay-sample-workspace/themes

	cp liferay-sample-workspace/modules/README.md liferay-sample-workspace/themes

	#
	# Client Extension: Sample Custom Element 3
	#

	rm -fr liferay-sample-workspace/client-extensions/liferay-sample-custom-element-3

	../tools/create_custom_element.sh liferay-sample-custom-element-3 angular

	mv liferay-sample-custom-element-3 liferay-sample-workspace/client-extensions
}

function main {
	check_blade

	refresh_liferay_sample_workspace
}

main "${@}"