#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/../../../../env/common.sh

cluster_set_up 1 "true" "-Xms512m -Xmx2048m"