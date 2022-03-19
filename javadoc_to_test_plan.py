from bs4 import BeautifulSoup
import os
import pandas as pd


PACKAGE_NAME = "utils"
CLASS_NAME = "ValueSanity"
FILE_PATH = f"docs/{PACKAGE_NAME}test/{CLASS_NAME}Test.html"

with open(FILE_PATH, "r") as f:
    source = f.read()

soup = BeautifulSoup(source, "lxml")

summary_table = soup.find_all('table')[1]
method_names = [row.find_all('span', {'class': 'memberNameLink'})[0].get_text() for row in summary_table.find_all('tr')[1:]]
method_javadocs = [div.get_text() for div in summary_table.find_all('div')]

if method_names[0] == "setUp":
    method_names = method_names[1:]
    method_javadocs = method_javadocs[1:]

tuplify = [(method_name, method_javadoc) for method_name, method_javadoc in zip(method_names, method_javadocs)]

print(f"Found {len(method_names)} tests!")

df = pd.DataFrame(tuplify, columns=['Test method', 'Test description'])
df.to_csv(os.path.basename(FILE_PATH).replace(".html", ".csv"), index=False)