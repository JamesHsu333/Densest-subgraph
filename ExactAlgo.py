import copy
import sys
from collections import defaultdict
import networkx as nx

f_g = open(sys.argv[1].replace(".txt", "")+"_graph.txt")
f_d = open(sys.argv[1].replace(".txt", "")+"_degree.txt")
edges = f_g.readlines()
vertices = f_d.readlines()
degree = defaultdict(int)
G=nx.DiGraph()
edge_count=len(edges)/2
vertex_count=len(vertices)
guess=edge_count/2
u=edge_count
l=0
edge_set = set()
cutset = set()
compare_set = set()
# Answer
maximum_degree=0.0
final_cutset=set()

def calculateDensity(edge_set, cutset):
    tmp=set()
    vertices=set()
    for k, v in sorted(cutset):
        if k!="s" and v!="t":
            tmp.add((k,v))
            tmp.add((v,k))
    for k, v in sorted(edge_set - tmp):
        vertices.add(k)
        vertices.add(v)
    vertex_count=len(vertices)
    edge_count=len(edge_set-tmp)/2
    print("|V|={}, |E|={}, d={}".format(vertex_count, edge_count, edge_count/vertex_count))
    return edge_count/vertex_count

def outputDSG(edge_set, cutset):
    tmp=set()
    vertices=set()
    for k, v in sorted(cutset):
        if k!="s" and v!="t":
            tmp.add((k,v))
            tmp.add((v,k))
    return (edge_set - tmp)

# Init
for e in edges:
    k, v = e.strip().split()
    edge_set.add((k, v))
    G.add_edge(k, v, capacity=1.0)
for val in vertices:
    v, d = val.strip().split()
    degree[v] = int(d)
    compare_set.add(("s", v))
for v, d in degree.items():
    G.add_edge("s", v, capacity=edge_count)
    G.add_edge(v, "t", capacity=edge_count+2*guess-d)

# ExactAlgo
while (u-l) >= (1/(vertex_count*(vertex_count-1))):
    guess = (u+l)/2
    for v, d in degree.items():
        G["s"][v]['capacity']=edge_count
        G[v]["t"]['capacity']=edge_count+2*guess-d
    _, partition = nx.minimum_cut(G, "s", "t")
    reachable, non_reachable = partition
    for u_t, nbrs in ((n, G[n]) for n in reachable):
        cutset.update((u_t, v) for v in nbrs if v in non_reachable)
    if cutset == compare_set:
        u = guess
        current_degree = calculateDensity(edge_set, cutset)
        if current_degree > maximum_degree:
            maximum_degree = current_degree
            final_cutset.clear()
            final_cutset = copy.deepcopy(cutset)
    else:
        l = guess
        current_degree = calculateDensity(edge_set, cutset)
        if current_degree > maximum_degree:
            maximum_degree = current_degree
            final_cutset.clear()
            final_cutset = copy.deepcopy(cutset)
    cutset.clear()

with open(sys.argv[1].replace(".txt", "")+"_dsg.txt", 'w') as file:
    dsg = outputDSG(edge_set, final_cutset)
    for k, v in sorted(dsg):
        file.write(k+" "+v+"\n")

with open("DSG_density.txt", 'a+') as file:
    file.write("\n"+sys.argv[1].replace(".txt", "") + " " + str(maximum_degree))
f_g.close()
f_d.close()
