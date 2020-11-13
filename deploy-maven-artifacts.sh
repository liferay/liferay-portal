#!/usr/bin/env bash

#set -v -x
set -u -e

version=7.0.6e
lexicon_version=1.0.25a
repo_url=http://jenkins.axiell.local:8081
repo_dir=~/.m2/repository
bundles_dir=/opt/java/liferay/arena-7.0.6-ga7/bundles.org

function deployFile {
  local file=${1}
  local packaging=${2}
  local version=${3}
  local groupId=${4:-com.liferay.portal}
  local dir=${groupId//./\/}
  echo "Deploying file: ${file} packaging: ${packaging}"
  tmp_file=$(mktemp)
  cp ${repo_dir}/${dir}/${file}/${version}/${file}-${version}.${packaging} ${tmp_file}
  mvn org.apache.maven.plugins:maven-deploy-plugin:2.8.2:deploy-file -DgroupId=${groupId} -DartifactId=${file} -Dfile=${tmp_file} -DrepositoryId=central -Dversion=${version} -Dpackaging=${packaging} -Durl=${repo_url}/artifactory/simple/ext-release-local/
  rm ${tmp_file}
}


file=${bundles_dir}/osgi/war/admin-theme.war
if [ ! -f ${bundles_dir}/osgi/war/admin-theme.war ]
then
	echo "Missing file: ${file}"
	exit 4
fi

tmp_file=$(mktemp)
file=com.liferay.portal.osgi
packaging=zip
rm -rf ${repo_dir}/com/liferay/portal/${file}/${version}
mkdir -p ${repo_dir}/com/liferay/portal/${file}/${version}
pushd ${bundles_dir}
zip -rX ${repo_dir}/com/liferay/portal/${file}/${version}/${file}-${version}.${packaging} osgi -x osgi/portal/com.liferay.portal.search.elasticsearch.jar -x osgi/war/porygon-theme.war osgi/war/westeros-bank-theme.war osgi/war/fjord-theme.war osgi/war/1975-london-theme.war
popd
deployFile ${file} ${packaging} ${version}

file=com.liferay.frontend.theme.unstyled
packaging=jar
groupId="com.liferay"
rm -rf ${repo_dir}/com/liferay/${file}/${version}
mkdir -p ${repo_dir}/com/liferay/${file}/${version}
cp ${bundles_dir}/osgi/modules/${file}.${packaging} ${repo_dir}/com/liferay/${file}/${version}/${file}-${version}.${packaging}
deployFile ${file} ${packaging} ${version} ${groupId}

packaging=jar
for file in com.liferay.portal.client com.liferay.portal.impl com.liferay.portal.kernel com.liferay.support.tomcat com.liferay.util.bridges com.liferay.util.java com.liferay.util.slf4j com.liferay.util.taglib
do
deployFile ${file} ${packaging} ${version}
done

file=com.liferay.portal.web
packaging=war
deployFile ${file} ${packaging} ${version}

file=com.liferay.webjars.lexicon
packaging=jar
groupId="com.liferay.webjars"
deployFile ${file} ${packaging} ${lexicon_version} ${groupId}



