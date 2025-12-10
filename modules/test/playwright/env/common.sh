#!/bin/bash

function cluster_set_up {
	default_set_up

	prepare_additional_bundles ${1} ${2}

	local slave_home="${LIFERAY_HOME}-${1}"

	cp "${CURRENT_DIR_NAME}/com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration.config" "${slave_home}/osgi/configs"

	sed -i "s/%LIFERAY_DOCKER_NETWORK_NAME%/${LIFERAY_DOCKER_NETWORK_NAME}/g" "${slave_home}/osgi/configs/com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration.config"

	rm -fr "${slave_home}/data"

	mkdir -p "${slave_home}/data"

	ln -s "${LIFERAY_HOME}/data/document_library" "${slave_home}/data"

	local domain

	for domain in "${slave_home}/routes/default/dxp/"*
	do
		sed -i 's/8080/9080/g' "${domain}"
	done

	rm -fr "${slave_home}/elasticsearch-sidecar"
	rm -fr "${slave_home}/osgi/state"
	rm -fr "${slave_home}/osgi/tomcat/work"
	rm -fr "${slave_home}/osgi/work"

	local node_home

	for node_home in "${LIFERAY_HOME}" "${slave_home}"
	do
		delete_property "${node_home}" "web.server.http.port"
		delete_property "${node_home}" "web.server.https.port"
	done

	update_property "${slave_home}" "module.framework.properties.osgi.console" "localhost:11313"

	start_additional_bundles ${1}
}

function combine_properties_files {
	local temp_properties_file=temp.properties

	echo "" > ${temp_properties_file}

	for properties_file in ${@}
	do
		if [ ! -f "${properties_file}" ]
		then
			continue
		fi

		echo -e "\n# From ${properties_file}\n" >> ${temp_properties_file}

		while IFS='=' read -r property_name property_value || [ -n "${property_name}" ]
		do
			if [[ ${property_name} =~ ^\ *# ]] || [[ ${property_name} =~ ^\ *\/\/ ]] || [[ -z "${property_name}" ]]
			then
				continue
			fi

			property_name=$(echo "${property_name}" | sed 's/^\ *//;s/\ *$//')
			property_value=$(echo "${property_value}" | sed 's/^\ *//;s/\ *$//')

			while [[ ${property_value} =~ \\$ ]]
			do
				read -r property_value_next

				property_value="${property_value%\\}${property_value_next}"
			done

			sed -i "/^${property_name}=/d" ${temp_properties_file}

			echo "${property_name}=${property_value}" >> ${temp_properties_file}
		done < "${properties_file}"
	done

	mv ${temp_properties_file} ${1}

	echo ""
	echo "##"
	echo "## ${1}"
	echo "##"
	echo ""

	cat ${1}
}

function default_set_up {
	update_portal_ext_properties

	start_default_app_server

	deploy_parent_project_osgi_modules

	deploy_project_osgi_modules

	deploy_parent_project_deploy_folder

	deploy_project_deploy_folder

	deploy_parent_project_osgi_configs

	deploy_project_osgi_configs

	deploy_parent_project_client_extensions

	deploy_project_client_extensions
}

function default_tear_down {
	stop_default_app_server
}

function delete_property {
	local liferay_home=${1}
	local properties_file
	local property_name=${2}

	for properties_file in ${liferay_home}/tomcat-*/webapps/ROOT/WEB-INF/classes/*.properties
	do
		sed -i "s/${property_name}=.*//g" "${properties_file}"
	done
}

function deploy_client_extensions {
	echo "Deploying client extensions."

	local client_extensions_list_file=${1}

	local client_extension_dir

	for client_extension_dir in $(get_client_extension_dirs ${client_extensions_list_file})
	do
		if [[ -d ${client_extension_dir} ]]
		then
			echo "Deploying ${client_extension_dir}."

			cd ${client_extension_dir}

			local gradlew=$(get_gradlew)

			${gradlew} deploy -Pliferay.workspace.home.dir=${LIFERAY_HOME}

			wait_for_portal_log_inactivity ${LIFERAY_HOME}
		else
			echo "Unable to find client extension in ${client_extension_dir}."
		fi
	done
}

function deploy_deploy_folder {
	mkdir -p ${LIFERAY_HOME}/deploy

	local playwright_project_dir=${1}

	if [[ -d ${playwright_project_dir}/env/deploy ]]
	then
		cp -r ${playwright_project_dir}/env/deploy/ ${LIFERAY_HOME}/deploy
	fi
}

function deploy_osgi_configs {
	mkdir -p ${LIFERAY_HOME}/osgi/configs

	local playwright_project_dir=${1}

	if [[ -d ${playwright_project_dir}/env/osgi/configs ]]
	then
		cp -r ${playwright_project_dir}/env/osgi/configs/. ${LIFERAY_HOME}/osgi/configs
	fi
}

function deploy_osgi_modules {
	local osgi_modules_list_file=${1}

	if [[ -n $(cat ${osgi_modules_list_file}) ]]
	then
		echo "Deploying OSGi modules in ${osgi_modules_list_file}."

		local osgi_module_name

		for osgi_module_name in $(cat ${osgi_modules_list_file})
		do
			local osgi_module_dir=$(find ${_PORTAL_PROJECT_DIR}/modules/apps -type d -name "${osgi_module_name}" | grep -v .releng | grep -v .npmscripts | grep -v node_modules)

			if [[ $(echo ${osgi_module_dir} | wc -w | grep -o -E '[0-9]+') > 1 ]]
			then
				echo "Duplicate OSGi modules found for ${osgi_module_name}:"

				printf "%s\n" ${osgi_module_dir}

				echo "Replace \"${osgi_module_name}\" in ${osgi_modules_list_file} with one of the following:"

				for dir in ${osgi_module_dir}
				do
					echo "${dir/${_PORTAL_PROJECT_DIR}\/modules\/}"
				done

				osgi_module_dir=$(echo ${osgi_module_dir} | awk '{print $1}')
			fi

			if [[ -f ${osgi_module_dir}/build.gradle ]]
			then
				echo "Deploying ${osgi_module_dir}."

				cd ${osgi_module_dir}

				local gradlew=$(get_gradlew)

				${gradlew} deploy

				wait_for_portal_log_inactivity ${LIFERAY_HOME}
			else
				echo "Unable to find OSGi module in ${osgi_module_dir}."
			fi
		done
	fi
}

function deploy_parent_project_client_extensions {
	for parent_playwright_project_dir in $(get_parent_playwright_project_dirs)
	do
		if [[ -f ${parent_playwright_project_dir}/env/client-extensions.list ]]
		then
			deploy_client_extensions ${parent_playwright_project_dir}/env/client-extensions.list
		fi
	done
}

function deploy_parent_project_deploy_folder {
	mkdir -p ${LIFERAY_HOME}/deploy

	for parent_playwright_project_dir in $(get_parent_playwright_project_dirs)
	do
		deploy_deploy_folder ${parent_playwright_project_dir}
	done
}

function deploy_parent_project_osgi_configs {
	mkdir -p ${LIFERAY_HOME}/osgi/configs

	for parent_playwright_project_dir in $(get_parent_playwright_project_dirs)
	do
		deploy_osgi_configs ${parent_playwright_project_dir}
	done
}

function deploy_parent_project_osgi_modules {
	for parent_playwright_project_dir in $(get_parent_playwright_project_dirs)
	do
		if [[ -f ${parent_playwright_project_dir}/env/osgi-modules.list ]]
		then
			deploy_osgi_modules ${parent_playwright_project_dir}/env/osgi-modules.list
		fi
	done
}

function deploy_project_client_extensions {
	local playwright_project_dir=$(get_playwright_project_dir)

	if [[ -f ${playwright_project_dir}/env/client-extensions.list ]]
	then
		deploy_client_extensions ${playwright_project_dir}/env/client-extensions.list
	fi
}

function deploy_project_deploy_folder {
	deploy_deploy_folder $(get_playwright_project_dir)
}

function deploy_project_osgi_configs {
	deploy_osgi_configs $(get_playwright_project_dir)
}

function deploy_project_osgi_modules {
	local playwright_project_dir=$(get_playwright_project_dir)

	if [[ -f ${playwright_project_dir}/env/osgi-modules.list ]]
	then
		deploy_osgi_modules ${playwright_project_dir}/env/osgi-modules.list
	fi
}

function get_absolute_dir {
	echo $(cd -- $(dirname -- $1) &> /dev/null && pwd)
}

function get_app_server_dir {
	if [[ "${APP_SERVER_TYPE}" == "tomcat" ]]
	then
		find "${1}" -maxdepth 2 -type d -name "${APP_SERVER_TYPE}*"
	else
		find "${1}" -maxdepth 2 -type d -name "${APP_SERVER_TYPE}*[0-9]]"
	fi
}

function get_client_extension_dir {
	local clients_extension_name=${1}

	local client_extension_dir=$(find ${_PORTAL_PROJECT_DIR}/workspaces -type d | grep "${client_extension_name}$" | grep -v .releng | grep -v .npmscripts | grep -v dist | grep -v node_modules)

	if [[ $(echo ${client_extension_dir} | wc -w | grep -o -E '[0-9]+') > 1 ]]
	then
		echo "Duplicate client extensions found for ${client_extension_name}:" >&2
		echo "%s\n" ${client_extension_dir} >&2
		echo "Replace \"${client_extension_name}\" in ${client_extensions_list_file} with one of the following:" >&2

		for dir in ${client_extension_dir}
		do
			echo "${dir/${_PORTAL_PROJECT_DIR}\/workspaces\/}" >&2
		done

		client_extension_dir=$(echo ${client_extension_dir} | awk '{print $1}')
	fi

	echo ${client_extension_dir}
}

function get_client_extension_dirs {
	local client_extensions_list_file=${1}

	local client_extension_dirs=()

	if [[ -f ${client_extensions_list_file} && -n $(cat ${client_extensions_list_file}) ]]
	then
		for client_extension_name in $(cat ${client_extensions_list_file})
		do
			client_extension_dirs+=($(get_client_extension_dir))
		done
	fi

	echo ${client_extension_dirs[@]}
}

function get_client_extension_workspace_portal_ext_properties_files {
	local client_extensions_list_file=${1}

	local workspace_dirs=()

	if [[ -f ${client_extensions_list_file} ]]
	then
		local workspace_dir

		for workspace_dir in $(get_workspace_dirs ${client_extensions_list_file})
		do
			local portal_ext_properties_file="${workspace_dir}configs/local/portal-ext.properties"

			workspace_dirs+=(${portal_ext_properties_file})
		done
	fi

	echo ${workspace_dirs[@]}
}

function get_gradlew {
	if [[ -e ./gradlew ]]
	then
		echo "$(pwd)/gradlew"
	elif [[ $(pwd) == / ]]
	then
		echo "Unable to find gradlew."

		exit 1
	else
		echo $(cd .. ; get_gradlew)
	fi
}

function get_parent_client_extension_workspace_portal_ext_properties_files {
	for parent_playwright_project_dir in $(get_parent_playwright_project_dirs)
	do
		if [[ -f ${parent_playwright_project_dir}/env/client-extensions.list ]]
		then
			get_client_extension_workspace_portal_ext_properties_files ${parent_playwright_project_dir}/env/client-extensions.list
		fi
	done
}

function get_parent_playwright_project_dirs {
	local playwright_project_dir=${1}

	if [[ "${playwright_project_dir}" == "" ]]
	then
		playwright_project_dir=$(get_playwright_project_dir)
	fi

	current_playwright_project_dir=${playwright_project_dir}

	while [[ "${current_playwright_project_dir}" != "/" ]] && [[ "${current_playwright_project_dir}" != "${_PLAYWRIGHT_BASE_DIR}" ]]
	do
		current_playwright_project_dir=$(dirname "${current_playwright_project_dir}")

		echo ${current_playwright_project_dir}
	done
}

function get_parent_portal_ext_properties_files {
	for parent_playwright_project_dir in $(reverse $(get_parent_playwright_project_dirs))
	do
		if [[ -f ${parent_playwright_project_dir}/env/portal-ext.properties ]]
		then
			echo ${parent_playwright_project_dir}/env/portal-ext.properties
		fi
	done
}

function get_playwright_base_dir {
	echo ${_PLAYWRIGHT_BASE_DIR}
}

function get_playwright_project_dir {
	find ${_PLAYWRIGHT_BASE_DIR} -name config.ts -type f -print | xargs grep "name: '${PLAYWRIGHT_PROJECT_NAME}'" | sed -n 's/\(.*\)\/config.ts.*/\1/p'
}

function get_portal_log_file_size {
	wc --lines --total=always ${1}/logs/liferay.*.log | grep total | awk '{print $1}'
}

function get_portal_project_dir {
	echo ${_PORTAL_PROJECT_DIR}
}

function get_project_client_extension_workspace_portal_ext_properties_files {
	local playwright_project_dir=$(get_playwright_project_dir)

	if [[ -f ${playwright_project_dir}/env/client-extensions.list ]]
	then
		get_client_extension_workspace_portal_ext_properties_files ${playwright_project_dir}/env/client-extensions.list
	fi
}

function get_tomcat_portal_ext_properties_file {
	find ${LIFERAY_HOME} -type f -name "portal-ext.properties"
}

function get_workspace_dirs {
	local client_extensions_list_file=${1}

	local workspace_dirs=()

	local client_extension_dir

	if [[ -f ${client_extensions_list_file} ]]
	then
		for client_extension_dir in $(get_client_extension_dirs ${client_extensions_list_file})
		do
			local workspace_dir=$(echo ${client_extension_dir} | sed 's/\(.*-workspace\/\).*$/\1/')

			if ! [[ "${workspace_dirs[@]}" =~ "${workspace_dir}" ]]; then
				workspace_dirs+=(${workspace_dir})
			fi
		done
	fi

	echo ${workspace_dirs[@]}
}

function main {
	validate_environment_variables

	set_variables
}

function reverse {
	local array=(${@})

	for ((i = ${#array[@]} - 1; i >= 0; i--))
	do
	  echo "${array[$i]}"
	done
}

function prepare_additional_bundles {
	for ((i = 0 ; i < ${1} ; i++))
	do
		local app_server_bundles_size=$((1 + ${i}))

		local leading_port_number=$((8 + ${app_server_bundles_size}))

		local liferay_home="${LIFERAY_HOME}-${app_server_bundles_size}"

		if [[ -d ${liferay_home} ]]
		then
			rm -fr ${liferay_home}
		fi

		cp -r ${LIFERAY_HOME} ${liferay_home}

		local app_server_dir=$(get_app_server_dir ${liferay_home})

		echo ${app_server_dir}

		sed -i "s/=\"8\([0-9]\{3\}\)\"/=\"${leading_port_number}\1\"/g" "${app_server_dir}/conf/server.xml"

		local osgi_console_port=$((11312 + ${app_server_bundles_size}))

		sed -i "s/11312/${osgi_console_port}/g" "${app_server_dir}/webapps/ROOT/WEB-INF/classes/portal-ext.properties"

		sed -i "s/channel-logic-name/channel-logic-name-${app_server_bundles_size}/g" "${app_server_dir}/webapps/ROOT/WEB-INF/classes/portal-ext.properties"
		sed -i "s|liferay.home=${LIFERAY_HOME}|liferay.home=${liferay_home}|g" "${app_server_dir}/webapps/ROOT/WEB-INF/classes/portal-ext.properties"

		chmod a+x ${app_server_dir}

		if [[ ${2} == "true" ]]
		then
			sed -i "s/lportal/lportal${app_server_bundles_size}/g" "${app_server_dir}/webapps/ROOT/WEB-INF/classes/portal-ext.properties"

			cd ${_PORTAL_PROJECT_DIR}

			ant -f build-test.xml rebuild-database-playwright
		fi
	done
}

function set_variables {
	local playwright_env_dir=$(dirname ${BASH_SOURCE[0]})

	_PLAYWRIGHT_BASE_DIR=$(get_absolute_dir ${playwright_env_dir}/../..)
	_PORTAL_PROJECT_DIR=$(get_absolute_dir ${playwright_env_dir}/../../../../..)
}

function start_additional_bundles {
	for ((i = 0 ; i < ${1} ; i++ ))
	do
		local app_server_bundles_size=$((1 + ${i}))

		local liferay_home="${LIFERAY_HOME}-${app_server_bundles_size}"

		local leading_port_number=$((8 + ${app_server_bundles_size}))

		local liferay_portal_url="${LIFERAY_PORTAL_URL/\:8/\:"${leading_port_number}"}"

		start_app_server ${liferay_home} ${liferay_portal_url}
	done
}

function start_analytics_cloud {
	cd ${_PORTAL_PROJECT_DIR}

	ant -f build-test-analytics-cloud.xml start-analytics-cloud
}

function start_app_server {
	local liferay_home=${1}

	local app_server_dir=$(get_app_server_dir ${liferay_home})

	if [[ "${APP_SERVER_TYPE}" == "jboss" ]]
	then
		ant -f build-test-jboss.xml setup-jboss-playwright

		cd ${app_server_dir}/standalone/deployments

		touch ROOT.war.dodeploy

		cd ${app_server_dir}/bin

		/bin/bash standalone.sh &
	elif [[ "${APP_SERVER_TYPE}" == "tomcat" ]]
	then
		cd ${app_server_dir}/bin

		/bin/bash catalina.sh run &
	elif [[ "${APP_SERVER_TYPE}" == "weblogic" ]]
	then
		ant -f build-test-weblogic.xml setup-weblogic-playwright

		cd ${app_server_dir}/domains/liferay

		/bin/bash startWeblogic.sh
	elif [[ "${APP_SERVER_TYPE}" == "wildfly" ]]
	then
		ant -f build-test-wildfly.xml setup-wildfly-playwright

		cd ${app_server_dir}/standalone/deployments

		touch ROOT.war.dodeploy

		cd ${app_server_dir}/bin

		/bin/bash standalone.sh &
	fi

	local liferay_portal_url=${2}

	while ! curl --output /dev/null --silent --head --fail ${liferay_portal_url}
	do
		sleep 5
	done

	wait_for_portal_log_inactivity ${liferay_home}

	echo "${liferay_portal_url} is now available."
}

function start_client_extension_spring_boot_application {
	local client_extension_dir=${_PORTAL_PROJECT_DIR}/${1}

	if [[ -d ${client_extension_dir} ]]
	then
		local spring_boot_class_name=$(find ${client_extension_dir} -name "*SpringBootApplication.java" | grep "SpringBootApplication.java" | xargs basename | sed 's/.java//')

		echo "Starting ${spring_boot_class_name}."

		cd ${client_extension_dir}

		local portal_url_hostname=$(echo ${LIFERAY_PORTAL_URL} | awk -F:// '{print $2}')

		echo "${portal_url_hostname}" > ${LIFERAY_HOME}/routes/default/dxp/com.liferay.lxc.dxp.domains
		echo "${portal_url_hostname}" > ${LIFERAY_HOME}/routes/default/dxp/com.liferay.lxc.dxp.main.domain
		echo "${portal_url_hostname}" > ${LIFERAY_HOME}/routes/default/dxp/com.liferay.lxc.dxp.mainDomain

		local portal_url_scheme=$(echo ${LIFERAY_PORTAL_URL} | awk -F:// '{print $1}')

		echo "${portal_url_scheme}" > ${LIFERAY_HOME}/routes/default/dxp/com.liferay.lxc.dxp.server.protocol

		$(get_gradlew) bootRun -Pliferay.workspace.home.dir=${LIFERAY_HOME} &

		local sleep_duration=60
		local sleep_interval=5
		local total_duration=0

		while ! curl --output /dev/null --silent --head --fail http://localhost:58081/ready
		do
			if [ ${total_duration} -ge ${sleep_duration} ]; then
				echo "Unable to start ${spring_boot_class_name}."

				exit 1
			fi

			sleep ${sleep_interval}

			total_duration=$((total_duration + sleep_interval))
		done

		sleep ${sleep_interval}

		echo "Started ${spring_boot_class_name}."
	else
		echo "The directory ${client_extension_dir} does not exist."
	fi
}

function start_default_app_server {
	start_app_server ${LIFERAY_HOME} ${LIFERAY_PORTAL_URL}
}

function stop_additional_bundles {
	default_tear_down

	for ((i = 0 ; i < ${1} ; i++))
	do
		local app_server_bundles_size=$((1 + ${i}))

		local liferay_home=${LIFERAY_HOME}-${app_server_bundles_size}

		local leading_port_number=$((8 + ${app_server_bundles_size}))

		local liferay_portal_url="${LIFERAY_PORTAL_URL/\:8/\:"${leading_port_number}"}"

		stop_app_server ${liferay_home} ${liferay_portal_url}
	done
}

function stop_analytics_cloud {
	cd ${_PORTAL_PROJECT_DIR}

	ant -f build-test-analytics-cloud.xml stop-analytics-cloud
}

function stop_app_server {
	local liferay_home=${1}

	cd $(get_app_server_dir ${liferay_home})/bin

	if [[ "${APP_SERVER_TYPE}" == "tomcat" ]]
	then
		/bin/bash shutdown.sh &
	else
		cd ${_PORTAL_PROJECT_DIR}

		ant -f build-test.xml stop-app-server
	fi

	local portal_url=${2}

	while curl --output /dev/null --silent --head --fail ${portal_url}
	do
		sleep 5
	done

	echo "${portal_url} is no longer available."
}

function stop_client_extension_spring_boot_application {
	local client_extension_dir=${_PORTAL_PROJECT_DIR}/${1}

	if [[ -d ${client_extension_dir} ]]
	then
		local spring_boot_class_name=$(find ${client_extension_dir} -name "*SpringBootApplication.java" | grep "SpringBootApplication.java" | xargs basename | sed 's/.java//')

		echo "Stopping ${spring_boot_class_name}."

		kill -SIGTERM $(jps | grep ${spring_boot_class_name} | awk '{print $1}')

		local sleep_duration=60
		local sleep_interval=5
		local total_duration=0

		while curl --output /dev/null --silent --head --fail http://localhost:58081/ready
		do
			if [ ${total_duration} -ge ${sleep_duration} ]; then
				echo "Unable to start ${spring_boot_class_name}."

				exit 1
			fi

			sleep ${sleep_interval}

			total_duration=$((total_duration + sleep_interval))
		done

		sleep ${sleep_interval}

		echo "Stopped ${spring_boot_class_name}."
	else
		echo "The directory ${client_extension_dir} does not exist."
	fi
}

function stop_default_app_server {
	stop_app_server ${LIFERAY_HOME} ${LIFERAY_PORTAL_URL}
}

function update_portal_ext_properties {
	combine_properties_files \
		$(get_tomcat_portal_ext_properties_file) \
		\
		$(get_parent_client_extension_workspace_portal_ext_properties_files) \
		\
		$(get_project_client_extension_workspace_portal_ext_properties_files) \
		\
		$(get_parent_portal_ext_properties_files) \
		\
		$(get_playwright_project_dir)/env/portal-ext.properties
}

function update_property {
	local liferay_home=${1}
	local properties_file
	local property_name=${2}
	local property_value=${3}

	for properties_file in ${liferay_home}/tomcat-*/webapps/ROOT/WEB-INF/classes/*.properties
	do
		sed -i "s/${property_name}=.*/${property_name}=${property_value}/g" "${properties_file}"
	done
}

function validate_environment_variables {
	if [[ "${LIFERAY_HOME}" == "" ]]
	then
		echo "Set the environment variable LIFERAY_HOME."

		exit 1
	fi

	if [[ "${LIFERAY_PORTAL_URL}" == "" ]]
	then
		echo "Set the environment variable LIFERAY_PORTAL_URL."

		exit 1
	fi

	if [[ "${PLAYWRIGHT_PROJECT_NAME}" == "" ]]
	then
		echo "Set the environment variable PLAYWRIGHT_PROJECT_NAME."

		exit 1
	fi
}

function wait_for_portal_log_inactivity {
	local portal_log_file_size=$(get_portal_log_file_size ${1})

	local sleep_interval=15
	local sleep_duration=180
	local total_duration=0

	sleep ${sleep_interval}

	while [[ ${portal_log_file_size} != $(get_portal_log_file_size ${1}) ]]
	do
		portal_log_file_size=$(get_portal_log_file_size ${1})

		if [[ ${total_duration} -ge ${sleep_duration} ]]
		then
			break
		fi

		sleep ${sleep_interval}

		total_duration=$((total_duration + sleep_interval))

		echo "Waiting for portal log inactivity..."
	done

	echo "No portal activity detected in ${sleep_interval}s."
}

main "${@}"