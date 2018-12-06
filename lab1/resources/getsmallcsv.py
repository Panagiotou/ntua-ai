import csv
i = 0
with open("newnodes.csv", "w") as f:
    for line in open("nodes.csv", encoding= 'latin-1'):
        f.write(line)
        i += 1;
        if(i == 1000):
            break;
