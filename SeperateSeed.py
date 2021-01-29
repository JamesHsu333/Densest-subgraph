f = open("Wiki-Vote-Seed_out.txt")
line = f.readline()
output = [line]
current_key= line.split()[0]
while line:
    line = f.readline()
    flag = False
    try:
        key = line.split()[0]
    except:
        flag = True
        with open("seed/"+current_key+".txt", 'w') as file:
            for v in output:
                file.write(v)
        break
    if current_key != key:
        with open("seed/"+current_key+".txt", 'w') as file:
            for v in output:
                file.write(v)
        current_key = key
        output = [line]
    else:
        output.append(line)
    if flag == True :
        break
f.close()
