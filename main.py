import requests
from bs4 import BeautifulSoup

base_url = "https://commerzbank-poland.breezy.hr"
url = "https://commerzbank-poland.breezy.hr/?#positions"
response = requests.get(url)
soup = BeautifulSoup(response.text, "html.parser")

position_details = soup.find_all("li", class_="position-details")

positions = []
for position_detail in position_details:
    position_link = position_detail.find("a").attrs["href"]
    positions.append(base_url + position_link)

type_mapping = {
    "%LABEL_POSITION_TYPE_FULL_TIME%": "Full Time",
    "%LABEL_POSITION_TYPE_OTHER%" : "Other",
    # next cases....
}

for position in positions:
    response = requests.get(position)
    soup = BeautifulSoup(response.text, "html.parser")

    if response.status_code == 200:
        title = soup.find("h1").text.strip() if soup.find("h1") else "Not Found"


        def get_info(class_name):
            li_element = soup.find("li", class_=class_name)
            if li_element:
                return li_element.find("span").text.strip()

            return "Not Found"


        job_info = {
            "Location": get_info("location"),
            "Type": type_mapping.get(get_info("type"), get_info("type")),
            "Department": get_info("department")
        }

        print("Title:", title)
        for key, value in job_info.items():
            print(f"{key}: {value}")

    else:
        print(f"Failed to retrieve page, status code: {response.status_code}")
