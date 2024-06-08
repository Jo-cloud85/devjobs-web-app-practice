export interface JobSummary {
    id: number,
    logo: string,
    logoBackground: string,
    postedAt: string,
    contract: string,
    position: string,
    company: string,
    location: string
}

export interface JobDetails {
    id: number,
    company: string,
    logo: string,
    logoBackground: string,
    position: string,
    postedAt: string,
    contract: string,
    location: string,
    website: string,
    apply: string,
    description: string,
    requirements: Requirements,
    role: Role
}

export interface Requirements {
    content: string,
    items: string[]
}

export interface Role {
    content: string,
    items: string[]
}