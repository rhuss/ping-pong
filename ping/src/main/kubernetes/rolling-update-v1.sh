#!/bin/sh
kubectl rolling-update ping-v2 --update-period=1s -f rc.yaml
