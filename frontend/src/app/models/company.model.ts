export interface Company {
    company: string;
    address: string;
    coordinates: [number, number];
    reviews: {
      date: { $date: number };
      comments: string[];
      score: number;
    }[];
    jobPostings: {
        position: string;
        postedAt: string;
        contract: string;
    }[];
}