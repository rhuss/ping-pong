#!/bin/sh
kubectl rolling-update peng --update-period=1s -f rc-2.yaml
