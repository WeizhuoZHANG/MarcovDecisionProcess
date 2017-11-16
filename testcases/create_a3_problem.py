#!/usr/bin/env python                                                                                            
import sys
import os
from optparse import OptionParser, OptionValueError
import random

p = OptionParser(usage="""usage: %prog [options] <venture_manager_type> <outputfile>
Creates a test-case for A3
""")
p.add_option("-v", action="store_true",        dest="verbose",  help="Verbose")
p.add_option("--seed", action="store", dest="seed", type="int", help="Seed for random generator.  If none is provided, the generator will be seeded from system time or whatever python does")
p.add_option("-g", action="store", dest="discount", type="float", default=0.975, help="Discount factor.  Default=%default")
p.add_option("-f", action="store", dest="fortnights", type="int", default=10, help="Number of fortnights.  Default=%default")
p.add_option("--selling_mean", action="store", dest="selling_mean", type="float", default=3.0, help="Mean selling price.  This is a float, but the final selling price for each venture will always be rounded to a positive integer.  Default=%default")
p.add_option("--selling_standard_deviation", action="store", dest="selling_standard_deviation", type="float", default=2.0, help="Standard deviation of selling price.  Default=%default")
p.add_option("--initial_funds_mean", action="store", dest="initial_funds_mean", type="float", default=1.0, help="Mean of initial funds.  This is a float, but the initial funds for each venture will always be rounded to a non-negative integer.  Default=%default")
p.add_option("--initial_funds_standard_deviation", action="store", dest="initial_funds_standard_deviation", type="float", default=1.0, help="Standard deviation of the initial funds.  Default=%default")
p.add_option("--bias", action="store", dest="bias", type="float", default=1.0, help="For numbers less than 1.0, the random probabilities will be biased towards small demands.  For numbers greater than 1.0, the random probabilities will be biased towards high demands.  Must be > 0.0.  Default=%default")
(opts, args) = p.parse_args()

if (len(args) != 2):
    sys.stderr.write("Need to specify venture-manager type and output file\n")
    sys.exit(1)

try:
    os.remove(args[0])
except:
    pass

ventureType = None
numVentures = None
maxManufacturingFunds = None
maxAdditionalFunds = None
if args[0] == "bronze":
    ventureType = "bronze"
    numVentures = 2
    maxManufacturingFunds = 3
    maxAdditionalFunds = 3
elif args[0] == "silver":
    ventureType = "silver"
    numVentures = 2
    maxManufacturingFunds = 5
    maxAdditionalFunds = 4
elif args[0] == "gold":
    ventureType = "gold"
    numVentures = 3
    maxManufacturingFunds = 6
    maxAdditionalFunds = 4
elif args[0] == "platinum":
    ventureType = "platinum"
    numVentures = 3
    maxManufacturingFunds = 8
    maxAdditionalFunds = 5
elif args[0] == "diamond":
    ventureType = "diamond"
    numVentures = 4
    maxManufacturingFunds = 10
    maxAdditionalFunds = 7
else:
    sys.stderr.write("Illegal venture type " + args[0] + "\n")
    sys.stderr.write("Must be either 'bronze', 'silver', 'gold', 'platinum' or 'diamond'\n")
    sys.exit(1)
if opts.verbose:
    sys.stdout.write("Venture type = " + ventureType + "\n")
    sys.stdout.write("Number of ventures = " + str(numVentures) + "\n")
    sys.stdout.write("Max manufacturing funds = " + str(maxManufacturingFunds) + "\n")
    sys.stdout.write("Max additional funds = " + str(maxAdditionalFunds) + "\n")

if (opts.fortnights < 1):
    sys.stderr.write("Number of fortnights must be positive\n")
    sys.exit(1)

random.seed(opts.seed)

if (opts.bias <= 0.0):
    sys.stderr.write("Bias must be positive\n")
    sys.exit(1)

f = open(args[1], 'w')
f.write(ventureType + "\n")
f.write(str(opts.discount) + "\n")
f.write(str(opts.fortnights) + "\n")

selling_prices = []
for v in range(numVentures):
    rand = random.gauss(opts.selling_mean, opts.selling_standard_deviation)
    tries = 1
    while (rand < 1.0):
        rand = random.gauss(opts.selling_mean, opts.selling_standard_deviation)
        tries += 1
        if (tries % 100 == 0):
            sys.stdout.write("Warning: 100 trials of choosing selling price.  Perhaps your selling_mean and selling_standard_deviation are suboptimal\n")
    selling_prices.append(int(round(rand)))
f.write(" ".join(map(str, selling_prices)) + "\n")

ini_funds = [maxManufacturingFunds + 1]
tries = 1
while (sum(ini_funds) > maxManufacturingFunds):
    ini_funds = []
    for v in range(numVentures):
        tries += 1
        rand = random.gauss(opts.initial_funds_mean, opts.initial_funds_standard_deviation)
        while (rand < 0.0):
            rand = random.gauss(opts.initial_funds_mean, opts.initial_funds_standard_deviation)
            tries += 1
        if (tries % 1000 == 0):
            sys.stdout.write("Warning: 1000 trials of choosing initial funds.  Perhaps your mean and standard_deviation are suboptimal\n")
        ini_funds.append(int(round(rand)))
f.write(" ".join(map(str, ini_funds)) + "\n")

for v in range(numVentures):
    for m in range(maxManufacturingFunds + 1):
        probs = [round(pow(opts.bias, i) * random.random(), 3) for i in range(maxManufacturingFunds + 1)]
        norm = sum(probs)
        probs = [round(p / norm, 3) for p in probs]
        probs[-1] = 1.0 - sum(probs[:-1])
        f.write(" ".join(map(str, probs)) + "\n")
f.close()


sys.exit(0)
