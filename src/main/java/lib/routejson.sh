#!/bin/bash

ip route | grep via | sed 's/ via /", "gateway":"/; s/ dev.*/" },/; s/^/{ "dst":"/; 1s/^/[/; $s/,$/]/' | tr '\r\n' ' '