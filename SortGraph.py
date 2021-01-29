import sys
from collections import defaultdict

f = open(sys.argv[1])
line = f.readline()
output = set()
degree = defaultdict(int)
output.add(" ".join(line.strip().split()))
output.add(" ".join(reversed(line.split())))
while line:
    line = f.readline()
    reverse = " ".join(reversed(line.split()))
    output.add(" ".join(line.strip().split()))
    output.add(reverse)

output.remove("")

with open(sys.argv[1].replace(".txt", "")+"_graph.txt", 'w') as file:
   for v in sorted(output):
        file.write(v+"\n")
        key = v.split()[0]
        if not degree[key]:
            degree[key]=1
        else:
            degree[key]+=1

with open(sys.argv[1].replace(".txt", "")+"_degree.txt", 'w') as file:
    for k, v in degree.items():
        file.write(k+" "+str(v)+"\n")

f.close()
