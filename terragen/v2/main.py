# ########################################
# Eric Mikulin, Terrain Generation Tool
# ########################################

import random

# Constants
SIZE = 100
MAX_HEIGHT = 255

# Diamond Square Algorithm
raw_map = [[0 for x in range(SIZE)] for x in range(SIZE)]

# Init corner values
raw_map[0][0] = gen_initial(MAX_HEIGHT)
raw_map[0][SIZE-1] = gen_initial(MAX_HEIGHT)
raw_map[SIZE-1][0] = gen_initial(MAX_HEIGHT)
raw_map[SIZE-1][SIZE-1] = gen_initial(MAX_HEIGHT)

# Run the Generation
run_step((0,0), (SIZE-1,SIZE-1))

# Recursivly generate the stuff
def run_step(top_left_cor, bot_right_cor):
    # Perform Diamond Step
    mid_point = (top_left_cor[0] - bot_right_cor[0], top_left_cor[1] - bot_right_cor[1])
    raw_map[mid_point[0], mid_point[1]] = gen_initial(MAX_HEIGHT)

    # Perform Square step

#Convert to Map

# Gen INIT height
def gen_initial(max_num):
    return random.random() * max_num
