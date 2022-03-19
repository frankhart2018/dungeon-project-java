import subprocess
from tqdm import tqdm
from threading import Thread
import time
import os


JAR_PATH = "./out/artifacts/pdp_project_3_jar/pdp-project-3.jar"
OUTPUT_DIR = f"./out-{int(time.time())}"
NUM_ITERS = 1_000
NUM_THREADS = 32
VALID_INVALID_DUNGEONS = {"valid": 0, "invalid": 0}

os.makedirs(OUTPUT_DIR, exist_ok=True)

def get_valid_invalid_dungeon(i, j):
    global VALID_INVALID_DUNGEONS

    output = subprocess.getoutput(f"java -jar {JAR_PATH}")

    with open(f"{OUTPUT_DIR}/output-{i}-{j}.txt", "w") as f:
        f.write(output)

    if "Dungeon created in" in output:
        VALID_INVALID_DUNGEONS["valid"] += 1
    else:
        VALID_INVALID_DUNGEONS["invalid"] += 1 

if __name__ == '__main__':
    done_count = 0
    for i in tqdm(range(0, NUM_ITERS, NUM_THREADS)):    
        remaining_count = NUM_ITERS - done_count
        num_threads = min(remaining_count, NUM_THREADS)
        threads = [Thread(target=get_valid_invalid_dungeon, args=(i, j)) for j in range(num_threads)]

        for thread in threads:
            thread.start()

        for thread in threads:
            thread.join()

        done_count += num_threads

    print(VALID_INVALID_DUNGEONS)