#!/usr/bin/env bash
set -eu

width=${1:-512}
height=${2:-512}
frame_rate=${3:-15}
palette="palette.png"
filters="fps=${frame_rate},scale=${width}:${height}:-1:flags=lanczos"

pushd captures/gif
ffmpeg -v warning -i %06d.png -vf "$filters,palettegen" -y $palette
ffmpeg -v warning -i %06d.png -i $palette -lavfi "$filters[x];[x][1:v]paletteuse" -y out.gif
rm $palette
mv out.gif ..
popd
