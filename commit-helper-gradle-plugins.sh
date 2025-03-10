cd modules/sdk/ant-bnd
../../../gradlew installCache updateFileVersions
git add "../../../*.markdown"
git add "../../../*.gradle"
git commit -m "LPD-X Use latest"

cd ../gradle-plugins-jasper-jspc
../../../gradlew installCache updateFileVersions
git add "../../../*.markdown"
git add "../../../*.gradle"
git commit --amend --no-edit

cd ../gradle-plugins
../../../gradlew installCache updateFileVersions
git add "../../../*.gradle"
git commit --amend --no-edit

cd ../gradle-plugins-defaults
../../../gradlew installCache updateFileVersions
git add "../../../*.gradle"
git commit --amend --no-edit

cd ../../..

git add ".m2-tmp/*.jar"
git add ".m2-tmp/*.pom"
git commit -m "LPD-X Fake gradle cache"