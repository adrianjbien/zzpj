package org.example;
import org.graalvm.polyglot.Context;


public class Main {
    public static void main(String[] args) {
        try (Context ctx = Context.create()) {
            ctx.eval("python", """
                    import json
                    import requests
                    from bs4 import BeautifulSoup
                    import logging
                    
                    logger = logging.getLogger(__name__)
                    logging.basicConfig(filename='example.log', encoding='utf-8', level=logging.DEBUG)
                    
                    base_urls = ["https://commerzbank-poland.breezy.hr", "https://voyagu.breezy.hr"]
                    
                    type_mapping = {
                                "%LABEL_POSITION_TYPE_FULL_TIME%": "Full Time",
                                "%LABEL_POSITION_TYPE_OTHER%": "Other",
                                '%LABEL_POSITION_TYPE_PART_TIME%': 'Part Time',
                                # next cases....
                            }
                    
                    
                    def get_info(class_name, soup):
                        li_element = soup.find("li", class_=class_name)
                        if li_element:
                            return li_element.find("span").text.strip()
                        return "Not Found"
                    
                    
                    def get_positions_json():
                        job_list = []
                        for base_url in base_urls:
                            response = requests.get(base_url)
                            soup = BeautifulSoup(response.text, "html.parser")
                    
                            position_details = soup.find_all("li", class_="position-details")
                    
                            positions_links = []
                            for position_detail in position_details:
                                position_link = position_detail.find("a").attrs["href"]
                                positions_links.append(base_url + position_link)
                    
                            for position in positions_links:
                                response = requests.get(position)
                                soup = BeautifulSoup(response.text, "html.parser")
                    
                                if response.status_code == 200:
                                    title = soup.find("h1").text.strip() if soup.find("h1") else "Not Found"
                    
                                    job_info = {
                                        "Title": title,
                                        "Location": get_info("location", soup),
                                        "Type": type_mapping.get(get_info("type", soup), get_info("type", soup)),
                                        "Department": get_info("department", soup)
                                    }
                    
                                    job_list.append(job_info)
                                    print(job_info)
                                else:
                                    logger.error(f"Failed to retrieve page, status code: {response.status_code}")
                        return job_list""");
        }
    }
}