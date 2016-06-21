#!/bin/sh
kubectl rolling-update peng-v2 --update-period=1s -f rc.yaml
