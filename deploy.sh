#!/bin/bash
echo "Publishing jar files ..."

gradle :publish -Prepo_user=$1 -Prepo_password=$2


