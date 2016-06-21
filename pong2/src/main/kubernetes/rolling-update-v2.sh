#!/bin/sh
kubectl rolling-update pong --update-period=1s -f rc-v2.yaml
