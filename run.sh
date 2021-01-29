str="seed/"
str1=".txt"
for d in seed/*.txt ; do
	if [[ $d == *"_out"* ]]; then
		continue
	fi
	tmp=(${d//$str/})
	t=(${tmp//$str1/})
	make run INPUT_CACHE=$t
done
