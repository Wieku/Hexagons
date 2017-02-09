#!/bin/bash

cd $1

cat > ranked.json <<EOF
{
  "rankedId": "$(cat /proc/sys/kernel/random/uuid)",
  "permissionId": $2,
  "essentialFiles": {
$(find . \( -name '*.lua' -or -name '*.hocon' -or -name "map.json" \) -printf "    \"%f\": \"<<SHA %f>>\",\n" | tr '\n' '#' | sed 's/\(.*\),.*/\1/g' | tr '#' '\n')
  }
}
EOF

for f in $(find . \( -name '*.lua' -or -name '*.hocon' -or -name "map.json" \) -printf "%f\n"); do
    sed "s/<<SHA $f>>/$(sha256sum $f | awk '{print $1}')/" -i ranked.json
done;

sha256sum ranked.json | awk '{print $1}'
