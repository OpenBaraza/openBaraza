#/bin/sh

cd $(dirname $0)

jar cf crb-africa.jar co/ke/transunion/crbws/ws/*.class co/ke/transunion/crbws/ws/impl/*.class

