#!/bin/sh
kubectl rolling-update pong-v2 --update-period=1s -f rc-v1.yaml
