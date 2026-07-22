#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

source _common.sh

function main {
	_create_worktree

	[[ ${LIFERAY_PROVISION:-} == none ]] && { echo "${WORKTREE_DIR}"; return 0; }

	_set_bundle_path

	if [[ ${LIFERAY_PROVISION:-} == fresh ]]
	then
		(cd "${WORKTREE_DIR}" && ANT_OPTS="-Xmx2560m" ant all >&2) || _die "\"ant all\" failed under ${WORKTREE_DIR}."
	else
		_reuse_worktree
	fi

	_set_port_offset

	_set_arquillian_port
	_set_data_guard_port
	_set_database
	_set_debug_port
	_set_elasticsearch_ports
	_set_glowroot_port
	_set_gogo_shell_port
	_set_gradle_paths
	_set_playwright_port
	_set_portal_home
	_set_portal_http_address
	_set_poshi_url
	_set_test_integration_port
	_set_tomcat_ports

	[[ -z ${LIFERAY_PROVISION_SKIP_TOMCAT:-} ]] && "$(_find_tomcat_dir "${BUNDLES_DIR}")/bin/catalina.sh" jpda start >&2

	echo "${WORKTREE_DIR}"
}

function _all_ports_free_for_offset {
	local offset="${1}"

	local base

	for base in 8080 8005 8009 8443 8000 11311 9201 9301 4000 32763 42763
	do
		nc -z localhost "$((base + offset))" >/dev/null 2>&1 && return 1
	done

	return 0
}

function _bundle_exists {
	local bundles_dir="${1}"

	local tomcat_glob="${bundles_dir}"/tomcat-*

	for candidate in ${tomcat_glob}
	do
		[[ -d ${candidate} ]] && return 0
	done

	return 1
}

function _collect_claimed_offsets {
	local repo_dir

	repo_dir="$(git -C "${WORKTREE_DIR}" rev-parse --show-toplevel)"

	local line worktree_path bundles offset_file

	while IFS= read -r line
	do
		[[ ${line} == worktree\ * ]] || continue

		worktree_path="${line#worktree }"

		[[ ${worktree_path} != ${WORKTREE_DIR} ]] || continue

		bundles="$(_find_app_server_parent_dir "${worktree_path}" 2>/dev/null)" || continue

		offset_file="${bundles}/.worktree-port-offset"

		[[ -f ${offset_file} ]] || continue

		cat "${offset_file}"
	done < <(git -C "${repo_dir}" worktree list --porcelain)
}

function _create_worktree {
	local input cwd name

	input="$(cat)"

	cwd="$(jq --exit-status --raw-output .cwd <<< "${input}")" || _die "The \"cwd\" field is missing from the hook input ${input}."
	name="$(jq --exit-status --raw-output .name <<< "${input}")" || _die "The \"name\" field is missing from the hook input ${input}."

	local main_worktree target_path

	main_worktree="$(git -C "${cwd}" worktree list --porcelain | grep --extended-regexp "^worktree " | head --lines=1 | _sed "s/^worktree //")"

	target_path="$(dirname "${main_worktree}")/$(basename "${main_worktree}")-${name}"

	if ! git -C "${cwd}" worktree list --porcelain | grep --fixed-strings --line-regexp --quiet "worktree ${target_path}"
	then
		if git -C "${cwd}" show-ref --quiet --verify "refs/heads/${name}"
		then
			git -C "${cwd}" worktree add "${target_path}" "${name}" >&2
		else
			git -C "${cwd}" worktree add -b "${name}" "${target_path}" HEAD >&2
		fi
	fi

	WORKTREE_DIR="${target_path}"
}

function _resolve_main_worktree_dir {
	local repo_dir

	repo_dir="$(git -C "${WORKTREE_DIR}" rev-parse --show-toplevel)"

	git -C "${repo_dir}" worktree list --porcelain | grep --extended-regexp "^worktree .*/liferay-portal\$" | head --lines=1 | _sed "s/^worktree //"
}

function _reuse_worktree {
	MAIN_WORKTREE_DIR="$(_resolve_main_worktree_dir)"

	if [[ -n ${MAIN_WORKTREE_DIR} && ${MAIN_WORKTREE_DIR} != ${WORKTREE_DIR} ]]
	then
		rsync \
			--archive \
			--exclude="/.git/" \
			--exclude="/.gradle/.tmp/" \
			--exclude="/.gradle/caches/" \
			--exclude="/.gradle/daemon/" \
			--exclude="/bundle/" \
			--ignore-existing \
			"${MAIN_WORKTREE_DIR}/" \
			"${WORKTREE_DIR}/"

		if [[ -d ${MAIN_WORKTREE_DIR}/.gradle/caches && ! -e ${WORKTREE_DIR}/.gradle/caches ]]
		then
			mkdir -p "${WORKTREE_DIR}/.gradle"

			if [[ $(uname) == Darwin ]]
			then
				rsync --archive --link-dest="${MAIN_WORKTREE_DIR}/.gradle/caches" "${MAIN_WORKTREE_DIR}/.gradle/caches/" "${WORKTREE_DIR}/.gradle/caches/"
			else
				cp --archive --link "${MAIN_WORKTREE_DIR}/.gradle/caches" "${WORKTREE_DIR}/.gradle/caches"
			fi
		fi
	fi

	if ! _bundle_exists "${BUNDLES_DIR}"
	then
		[[ -n ${MAIN_WORKTREE_DIR} && ${MAIN_WORKTREE_DIR} != ${WORKTREE_DIR} ]] || _die "The main worktree is missing, so the bundle cannot be copied."

		local main_bundles

		main_bundles="$(_find_app_server_parent_dir "${MAIN_WORKTREE_DIR}")" || _die "The \"app.server.parent.dir\" property is undefined for ${MAIN_WORKTREE_DIR}."

		[[ -d ${main_bundles} ]] || _die "Main bundle directory ${main_bundles} does not exist."

		mkdir -p "${BUNDLES_DIR}"

		if [[ $(uname) == Darwin ]]
		then
			rsync --archive "${main_bundles}/" "${BUNDLES_DIR}/"
		else
			cp --archive "${main_bundles}/." "${BUNDLES_DIR}"
		fi

		_bundle_exists "${BUNDLES_DIR}" || _die "Bundle copy finished but no \"tomcat-*\" directory exists under ${BUNDLES_DIR}."
	fi
}

function _set_arquillian_port {
	local target=$((32763 + OFFSET))

	local config_file="${BUNDLES_DIR}/osgi/configs/com.liferay.arquillian.extension.junit.bridge.connector.ArquillianConnector.config"

	mkdir -p "$(dirname "${config_file}")"

	_atomic_write "${config_file}" <<EOF
port="${target}"
EOF

	local gradle_file="${WORKTREE_DIR}/.gradle/gradle.properties"

	[[ -f ${gradle_file} ]] || return 0

	_set_property "${gradle_file}" systemProp.liferay.arquillian.port "${target}"
}

function _set_bundle_path {
	BUNDLES_DIR="${WORKTREE_DIR}/bundle"

	_atomic_write "${WORKTREE_DIR}/app.server.${USER}.properties" <<< "app.server.parent.dir=\${project.dir}/bundle"
}

function _set_data_guard_port {
	local file="${BUNDLES_DIR}/osgi/configs/com.liferay.data.guard.connector.DataGuardConnector.config"

	mkdir -p "$(dirname "${file}")"

	local target=$((42763 + OFFSET))

	_atomic_write "${file}" <<EOF
port="${target}"
EOF
}

function _set_debug_port {
	local file

	file="$(_find_tomcat_dir "${BUNDLES_DIR}")/bin/setenv.sh"

	local target=$((8000 + OFFSET))

	[[ -f ${file} ]] || _die "${file} is missing."

	_set_worktree_paths "${file}"

	_set_property "${file}" JPDA_ADDRESS "\"${target}\""
}

function _set_elasticsearch_ports {
	local configs_dir="${BUNDLES_DIR}/osgi/configs"

	mkdir -p "${configs_dir}"

	local es_version=elasticsearch8

	if [[ -f ${configs_dir}/com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration.config ]]
	then
		es_version=elasticsearch7
	elif [[ -f ${configs_dir}/com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConfiguration.config ]]
	then
		es_version=elasticsearch8
	fi

	local file="${configs_dir}/com.liferay.portal.search.${es_version}.configuration.ElasticsearchConfiguration.config"
	local sidecar=$((9201 + OFFSET))
	local transport=$((9301 + OFFSET))

	_atomic_write "${file}" <<EOF
sidecarHttpPort="${sidecar}"
transportTcpPort="${transport}"
networkBindHost="127.0.0.1"
networkPublishHost="127.0.0.1"
EOF
}

function _set_glowroot_port {
	local file="${BUNDLES_DIR}/glowroot/admin.json"
	local target=$((4000 + OFFSET))

	if [[ ! -f ${file} ]]
	then
		return
	fi

	local current

	current="$(jq .web.port "${file}" 2>/dev/null || echo null)"

	if [[ ${current} == ${target} ]]
	then
		return
	fi

	local tmp="${file}.tmp"

	jq ".web.port = ${target}" "${file}" > "${tmp}" && mv "${tmp}" "${file}"
}

function _set_gogo_shell_port {
	local target=$((11311 + OFFSET))

	local developer_file

	developer_file="$(_find_tomcat_dir "${BUNDLES_DIR}")/webapps/ROOT/WEB-INF/classes/portal-developer.properties"

	[[ -f ${developer_file} ]] || _die "${developer_file} is missing."

	_set_property "${developer_file}" module.framework.properties.osgi.console "localhost:${target}"
	_set_property "${BUNDLES_DIR}/portal-ext.properties" module.framework.properties.osgi.console "${target}"
}

function _set_gradle_paths {
	local file="${WORKTREE_DIR}/.gradle/gradle.properties"

	[[ -f ${file} ]] || return 0

	local main_worktree

	main_worktree="$(_resolve_main_worktree_dir)"

	[[ -n ${main_worktree} && ${main_worktree} != ${WORKTREE_DIR} ]] || return 0

	local main_bundles_literal

	main_bundles_literal="$(_get_property "${file}" "liferay\.home")"

	[[ -n ${main_bundles_literal} ]] || _die "The \"liferay.home\" property is missing from ${file}."

	_sed \
		--in-place \
		--expression "s|${main_bundles_literal}/|${BUNDLES_DIR}/|g" \
		--expression "s|${main_bundles_literal}\$|${BUNDLES_DIR}|g" \
		--expression "s|${main_worktree}/|${WORKTREE_DIR}/|g" \
		"${file}"
}

function _set_playwright_port {
	local file="${WORKTREE_DIR}/modules/test/playwright/.env.local"
	local http_port=$((8080 + OFFSET))

	mkdir -p "$(dirname "${file}")"

	_atomic_write "${file}" <<EOF
PORTAL_URL=http://localhost:${http_port}
EOF
}

function _set_port_offset {
	local offset_file="${BUNDLES_DIR}/.worktree-port-offset"

	if [[ -f ${offset_file} ]]
	then
		OFFSET="$(cat "${offset_file}")"

		return
	fi

	local lock_file

	lock_file="$(git -C "${WORKTREE_DIR}" rev-parse --path-format=absolute --git-common-dir)/.worktree-port-offset.lock"

	local lock_dir="${lock_file}.d"

	local lock_fd
	local use_flock=0

	command -v flock >/dev/null 2>&1 && use_flock=1

	if [[ ${use_flock} -eq 1 ]]
	then
		exec {lock_fd}>"${lock_file}"

		flock "${lock_fd}"
	else
		local waited=0

		until mkdir "${lock_dir}" 2>/dev/null
		do
			sleep 1

			((++waited))

			[[ ${waited} -lt 120 ]] || _die "The port offset lock at ${lock_dir} could not be acquired."
		done
	fi

	local claimed_offsets

	claimed_offsets=" $(_collect_claimed_offsets | tr "\n" " ") "

	local offset

	for offset in $(seq 1 99)
	do
		[[ ${claimed_offsets} != *" ${offset} "* ]] || continue

		if _all_ports_free_for_offset "${offset}"
		then
			OFFSET="${offset}"
			echo "${OFFSET}" > "${offset_file}"

			break
		fi
	done

	if [[ ${use_flock} -eq 1 ]]
	then
		exec {lock_fd}>&-
	else
		rmdir "${lock_dir}" 2>/dev/null || true
	fi

	[[ -f ${offset_file} ]] || _die "No free port offset is available between 1 and 99."
}

function _set_portal_home {
	local file="${BUNDLES_DIR}/portal-ext.properties"

	_set_worktree_paths "${file}"

	_set_property "${file}" liferay.home "${BUNDLES_DIR}"
}

function _set_portal_http_address {
	local http_port=$((8080 + OFFSET))

	_set_property "${BUNDLES_DIR}/portal-ext.properties" portal.instance.inet.socket.address "localhost:${http_port}"
}

function _set_poshi_url {
	local file="${WORKTREE_DIR}/test.${USER}.properties"
	local http_port=$((8080 + OFFSET))

	_set_property "${file}" default.portal.url "http://localhost:${http_port}"
	_set_property "${file}" instance.url "http://localhost:${http_port}"
	_set_property "${file}" test.url "http://localhost:${http_port}"
}

function _set_property {
	local file="${1}"
	local key="${2}"
	local value="${3}"

	touch "${file}"

	local escaped="${key//./\\.}"

	_sed --in-place --regexp-extended --expression "/^[[:space:]]*${escaped}=/d" "${file}"

	if [[ -s ${file} ]] && [[ -n $(tail --bytes=1 "${file}") ]]
	then
		echo "" >> "${file}"
	fi

	echo "${key}=${value}" >> "${file}"
}

function _set_test_integration_port {
	local file="${WORKTREE_DIR}/.gradle/init.d/worktree-ports.gradle"

	mkdir -p "$(dirname "${file}")"

	local http_port=$((8080 + OFFSET))

	_atomic_write "${file}" <<EOF
allprojects {
	afterEvaluate { project ->
		def extension = project.extensions.findByName("testIntegrationTomcat")

		if (extension != null) {
			extension.portNumber = ${http_port}
		}
	}
}
EOF
}

function _set_tomcat_ports {
	local file

	file="$(_find_tomcat_dir "${BUNDLES_DIR}")/conf/server.xml"

	local target_http=$((8080 + OFFSET))
	local target_shutdown=$((8005 + OFFSET))
	local target_ajp=$((8009 + OFFSET))
	local target_https=$((8443 + OFFSET))

	_sed \
		--in-place \
		--regexp-extended \
		--expression "/<Server/s/port=\"[0-9]+\"/port=\"${target_shutdown}\"/" \
		--expression "/protocol=\"HTTP\\/1\\.1\"/s/port=\"[0-9]+\"/port=\"${target_http}\"/" \
		--expression "/protocol=\"org\\.apache\\.coyote\\.http11\\.Http11NioProtocol\"/s/port=\"[0-9]+\"/port=\"${target_https}\"/" \
		--expression "/<Connector protocol=\"AJP\\/1\\.3\"/,/\\/>/s/^([[:space:]]+)port=\"[0-9]+\"/\\1port=\"${target_ajp}\"/" \
		--expression "s/redirectPort=\"[0-9]+\"/redirectPort=\"${target_https}\"/g" \
		"${file}"
}

function _set_worktree_paths {
	local file="${1}"

	local main_bundles main_tomcat main_worktree

	main_worktree="$(_resolve_main_worktree_dir)"

	[[ -n ${main_worktree} && ${main_worktree} != ${WORKTREE_DIR} ]] || return 0

	main_bundles="$(_find_app_server_parent_dir "${main_worktree}" 2>/dev/null)" || return 0
	main_tomcat="$(_find_tomcat_dir "${main_bundles}" 2>/dev/null)" || return 0

	[[ -f ${file} ]] || return 0

	_sed \
		--in-place \
		--expression "s|${main_tomcat}/|$(_find_tomcat_dir "${BUNDLES_DIR}")/|g" \
		--expression "s|${main_bundles}/|${BUNDLES_DIR}/|g" \
		--expression "s|${main_worktree}/|${WORKTREE_DIR}/|g" \
		"${file}"
}

main "${@}"