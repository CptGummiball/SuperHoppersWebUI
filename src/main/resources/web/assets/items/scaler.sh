#!/bin/bash

# Überprüfe die Bildgröße und skaliere, falls nötig
for bild in *.png; do
    if identify -format '%w %h' "$bild" | grep -q "^\(16\)\s\(16\)"; then
        echo "$bild ist bereits 16x16px."
    else
        echo "Skaliere $bild auf 16x16px."
        convert "$bild" -resize 16x16\! "$bild"
    fi
done
