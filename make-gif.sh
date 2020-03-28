#!/usr/bin/env bash

palette="palette.png"
filters="fps=15,scale=1024:-1:flags=lanczos"

pushd frames
ffmpeg -v warning -i %04d.png -vf "$filters,palettegen" -y $palette
ffmpeg -v warning -i %04d.png -i $palette -lavfi "$filters[x];[x][1:v]paletteuse" -y out.gif
rm $palette
mv out.gif ..
popd
