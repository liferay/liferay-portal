#!/usr/bin/env bash

function main {
	cd "$(git rev-parse --show-toplevel)" || exit 1

	local path

	while IFS= read -r path
	do
		local marker_files="bnd.bnd build.xml gulpfile.js src/main/resources/application.properties"
		local themes=true

		if echo "${path}" | grep --extended-regexp "^workspaces/[^/]+-workspace/" > /dev/null
		then
			marker_files="bnd.bnd client-extension.yaml"
			themes=false
		fi

		local dir
		local module=-

		dir=$(dirname "${path}")

		while [[ ${dir} != . ]] &&
			  [[ ${dir} != / ]]
		do
			if _has_marker_file "${dir}" "${marker_files}" "${1}" ||
			   { [[ ${themes} == true ]] && _is_theme "${dir}" "${1}"; }
			then
				module=${dir}
			fi

			dir=$(dirname "${dir}")
		done

		echo "${module} ${path}"
	done
}

function _has_marker_file {
	if [[ ${1} == modules ]]
	then
		return 1
	fi

	local marker_file

	for marker_file in ${2}
	do
		if [[ -e ${1}/${marker_file} ]]
		then
			return 0
		fi
	done

	if [[ -d ${1} ]]
	then
		return 1
	fi

	for marker_file in ${2}
	do
		if git cat-file -e "${3}:${1}/${marker_file}" 2> /dev/null
		then
			return 0
		fi
	done

	return 1
}

function _is_theme {
	if [[ ${1} == modules ]]
	then
		return 1
	fi

	if [[ -d ${1} ]]
	then
		grep --quiet '"liferayTheme"' "${1}/package.json" 2> /dev/null

		return
	fi

	git show "${2}:${1}/package.json" 2> /dev/null | grep --quiet '"liferayTheme"'
}

main "${@}"