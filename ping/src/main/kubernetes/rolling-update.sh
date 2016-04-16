#!/bin/sh
kubectl rolling-update ping --update-period=1s -f rc-2.yaml
